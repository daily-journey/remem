package com.laev.reminder.service

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.dto.GetReviewItemsTodayResponse
import com.laev.reminder.entity.ReviewItem
import com.laev.reminder.entity.Member
import com.laev.reminder.entity.MemorizationLog
import com.laev.reminder.entity.ReviewDatetime
import com.laev.reminder.enum.ReviewItemStatus
import com.laev.reminder.exception.ItemAlreadyDeletedException
import com.laev.reminder.exception.ItemCreationException
import com.laev.reminder.exception.ItemNotFoundException
import com.laev.reminder.repository.ReviewItemRepository
import com.laev.reminder.repository.MemorizationLogRepository
import com.laev.reminder.repository.ReviewDatetimeRepository
import com.laev.reminder.service.dto.ReviewItemDetails
import com.laev.reminder.service.dto.ReviewItemMemorizationCount
import com.laev.reminder.utils.CycleCalculator
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Service
class ReviewItemService(
    private val reviewItemRepository: ReviewItemRepository,
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
    private val memorizationLogRepository: MemorizationLogRepository,
) {
    fun getReviewItems(datetime: OffsetDateTime?, member: Member): List<ReviewItem> {
        if (datetime == null) {
            return reviewItemRepository.findAllByMemberId(member.id!!)
        }

        val reviewDatetimes = reviewDatetimeRepository.findByDatetimeRange(datetime)

        return reviewDatetimes.map { it.reviewItem }
    }

    fun getReviewItemMemorizationCount(itemId: Long): ReviewItemMemorizationCount {
        return memorizationLogRepository.findMemorizationCountsByItemId(itemId)
    }

    fun getReviewItemsOfToday(member: Member): List<GetReviewItemsTodayResponse> {
        val nowDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        val items = reviewItemRepository.findReviewItemAndMemorizationLogByReviewDatesAndMemberId(nowDatetime, member.id!!)

        return items.map {
            val status = when (it.isMemorized) {
                true -> ReviewItemStatus.MEMORIZED
                false -> ReviewItemStatus.REMIND_LATER
                null -> ReviewItemStatus.NO_ACTION
            }
            GetReviewItemsTodayResponse(
                id = it.id,
                mainText = it.mainText,
                status = status,
            )
        }
    }

    fun getReviewItemDetail(member: Member, itemId: Long): ReviewItemDetails {
        val item = reviewItemRepository.findByIdAndMemberId(itemId, member.id!!)
            ?: throw ItemNotFoundException(itemId)

        val nowDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        val upcomingReviewDates = reviewItemRepository.findUpcomingReviewDatetimeByMemberIdAndReviewItemId(itemId, nowDatetime)
        val remindTomorrowDates = reviewItemRepository.findRemindTomorrowReviewDatetimeByMemberIdAndReviewItemId(itemId)
        val memorizedDates = reviewItemRepository.findMemorizedReviewDatetimeByMemberIdAndReviewItemId(itemId)
        val skippedDates = reviewItemRepository.findSkippedReviewDatetimeByMemberIdAndReviewItemId(itemId, nowDatetime)

        return ReviewItemDetails(
            id = itemId,
            mainText = item.mainText,
            subText = item.subText,
            isRecurring = item.isRecurring,
            upcomingReviewDates = upcomingReviewDates,
            remindTomorrowDates = remindTomorrowDates,
            memorizedDates = memorizedDates,
            skippedDates = skippedDates,
        )
    }

    @Transactional
    fun addReviewItem(request: AddItemRequest, member: Member) {
        try {
            val createDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS) // save time in UTC
            val cycles = listOf(1, 3, 7, 21)
            val reviewDates = CycleCalculator.getReviewDates(createDatetime, cycles)

            val zoneOffset = request.offset.toZoneOffset()
            val zonedCreatedDatetime = createDatetime.withOffsetSameInstant(zoneOffset) // Convert to local time with the applied zoneOffset

            val newReviewItem = reviewItemRepository.save(
                ReviewItem(
                    mainText = request.mainText,
                    subText = request.subText,
                    member = member,
                    reviewDates = reviewDates.toString(),
                )
            )

            for (cycle in cycles) {
                val startDatetime = CycleCalculator.getUTCStartDatetime(zonedCreatedDatetime, cycle, zoneOffset)
                val endDatetime = startDatetime.plusHours(24)

                reviewDatetimeRepository.save(
                    ReviewDatetime(
                        start = startDatetime,
                        end = endDatetime,
                        reviewItem = newReviewItem,
                    )
                )
            }
        } catch (e: DataIntegrityViolationException) {
            throw ItemCreationException("Failed to create item due to data integrity violation.")
        } catch (e: Exception) {
            throw ItemCreationException("An unexpected error occurred while creating the item.")
        }
    }

    @Transactional
    fun updateMemorization(itemId: Long, isMemorized: Boolean, offset: ZoneOffset) {
        val item = reviewItemRepository.findById(itemId).orElseThrow {
            ItemNotFoundException(itemId)
        }
        createMemorizationLog(isMemorized, item)

        // if not memorized, add a new review date for tomorrow
        if (!isMemorized) {
            createReviewDate(itemId, offset, 1)
        }
    }

    private fun createMemorizationLog(isMemorized: Boolean, reviewItem: ReviewItem) {
        val createdDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)

        memorizationLogRepository.save(
            MemorizationLog(
                isMemorized = isMemorized,
                createdDatetime = createdDatetime,
                reviewItem = reviewItem,
            )
        )
    }

    private fun createReviewDate(itemId: Long, offset: ZoneOffset, cycle: Int) {
        val zoneOffset = offset.toZoneOffset()
        val createdDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        val zonedCreatedDatetime = createdDatetime.withOffsetSameInstant(zoneOffset)
        val startDatetime = CycleCalculator.getUTCStartDatetime(zonedCreatedDatetime, cycle, zoneOffset)
        val endDatetime = startDatetime.plusHours(24)
        val item = reviewItemRepository.getReferenceById(itemId)

        // avoid duplicated row insertion
        val duplicatedReviewDatetime = reviewDatetimeRepository.findByStartAndItemId(startDatetime, itemId)
        if (duplicatedReviewDatetime.isEmpty()) {
            reviewDatetimeRepository.save(
                ReviewDatetime(
                    start = startDatetime,
                    end = endDatetime,
                    reviewItem = item
                )
            )
        }
    }

    private fun ZoneOffset.toZoneOffset(): ZoneOffset {
        val offsetHours = this.totalSeconds / 3600 // Convert the offset to hours
        return ZoneOffset.ofHours(offsetHours)
    }

    @Transactional
    fun deleteReviewItem(itemId: Long) {
        val item = reviewItemRepository.findById(itemId).orElseThrow {
            ItemNotFoundException(itemId)
        }

        if (item.isDeleted) {
            throw ItemAlreadyDeletedException()
        }

        item.isDeleted = true
        reviewItemRepository.save(item)
    }
}