package com.laev.reminder.service

import com.laev.reminder.component.ReviewDatetimeComponent
import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.dto.GetReviewItemsTodayResponse
import com.laev.reminder.entity.ReviewItem
import com.laev.reminder.entity.Member
import com.laev.reminder.entity.MemorizationLog
import com.laev.reminder.entity.ReviewDatetime
import com.laev.reminder.enum.ReviewItemStatus
import com.laev.reminder.exception.ConflictException
import com.laev.reminder.exception.ItemAlreadyDeletedException
import com.laev.reminder.exception.ItemCreationException
import com.laev.reminder.exception.ItemNotFoundException
import com.laev.reminder.repository.ReviewItemRepository
import com.laev.reminder.repository.MemorizationLogRepository
import com.laev.reminder.repository.ReviewDatetimeRepository
import com.laev.reminder.service.dto.ReviewItemDetails
import com.laev.reminder.service.dto.ReviewItemMemorizationCount
import com.laev.reminder.utils.CycleCalculator
import com.laev.reminder.utils.DateTimeUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class ReviewItemService(
    private val reviewItemRepository: ReviewItemRepository,
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
    private val memorizationLogRepository: MemorizationLogRepository,
    private val reviewDatetimeComponent: ReviewDatetimeComponent,
) {
    fun getReviewItems(datetime: OffsetDateTime?, member: Member): List<ReviewItem> {
        if (datetime == null) {
            return reviewItemRepository.findByMemberIdAndIsDeletedFalse(member.id)
        }

        val reviewDatetimes = reviewDatetimeRepository.findByDatetimeRange(datetime)

        return reviewDatetimes.map { it.reviewItem }
    }

    fun getReviewItemMemorizationCount(itemId: Long): ReviewItemMemorizationCount {
        return memorizationLogRepository.findMemorizationCountsByItemId(itemId)
    }

    fun getReviewItemsOfToday(member: Member): List<GetReviewItemsTodayResponse> {
        val nowDatetime = DateTimeUtils.getCurrentUtcTime()
        val items = reviewItemRepository.findReviewItemAndMemorizationLogByNowDatetimeAndMemberId(nowDatetime, member.id)

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
        val item = findItemByMember(member, itemId)
        val nowDatetime = DateTimeUtils.getCurrentUtcTime()

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
            val cycles = listOf(1, 3, 7, 21)
            val zoneOffset = request.offset.toZoneOffset()

            val newReviewItem = reviewItemRepository.save(
                ReviewItem(
                    mainText = request.mainText,
                    subText = request.subText,
                    member = member,
                )
            )

            reviewDatetimeComponent.createReviewDatetimeCycle(zoneOffset, cycles, newReviewItem)
        } catch (e: DataIntegrityViolationException) {
            throw ItemCreationException("Failed to create item due to data integrity violation.")
        } catch (e: Exception) {
            throw ItemCreationException("An unexpected error occurred while creating the item.")
        }
    }

    @Transactional
    fun updateMemorization(member: Member, itemId: Long, isMemorized: Boolean, offset: ZoneOffset) {
        val item = findItemByMember(member, itemId)

        createMemorizationLog(isMemorized, item)

        // if not memorized, add a new review date for tomorrow
        if (!isMemorized) {
            createReviewDate(itemId, offset, 1)
        }
    }

    private fun createMemorizationLog(isMemorized: Boolean, reviewItem: ReviewItem) {
        val nowDatetime = DateTimeUtils.getCurrentUtcTime()
        memorizationLogRepository.save(
            MemorizationLog(
                isMemorized = isMemorized,
                createdDatetime = nowDatetime,
                reviewItem = reviewItem,
            )
        )
    }

    private fun createReviewDate(itemId: Long, offset: ZoneOffset, cycle: Int) {
        val zoneOffset = offset.toZoneOffset()
        val nowDatetime = DateTimeUtils.getCurrentUtcTime()
        val zonedCreatedDatetime = nowDatetime.withOffsetSameInstant(zoneOffset)
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
    fun deleteReviewItem(member: Member, itemId: Long) {
        val item = reviewItemRepository.findByIdAndMemberId(itemId, member.id)
            ?: throw ItemNotFoundException(itemId)

        if (item.isDeleted) {
            throw ConflictException("Item is already deleted.")
        }

        item.isDeleted = true
        reviewItemRepository.save(item)
    }

    private fun findItemByMember(member: Member, itemId: Long): ReviewItem {
        val item = reviewItemRepository.findByIdAndMemberId(itemId, member.id)
            ?: throw ItemNotFoundException(itemId)

        if (item.isDeleted) {
            throw ItemAlreadyDeletedException()
        }

        return item
    }
}
