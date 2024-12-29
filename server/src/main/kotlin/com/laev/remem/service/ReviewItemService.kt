package com.laev.remem.service

import com.laev.remem.component.ReviewDatetimeComponent
import com.laev.remem.dto.AddItemRequest
import com.laev.remem.dto.GetReviewItemsTodayResponse
import com.laev.remem.entity.ReviewItem
import com.laev.remem.entity.Member
import com.laev.remem.entity.MemorizationLog
import com.laev.remem.entity.ReviewDatetime
import com.laev.remem.enum.ReviewItemStatus
import com.laev.remem.exception.ConflictException
import com.laev.remem.exception.ItemAlreadyDeletedException
import com.laev.remem.exception.ItemCreationException
import com.laev.remem.exception.ItemNotFoundException
import com.laev.remem.repository.ReviewItemRepository
import com.laev.remem.repository.MemorizationLogRepository
import com.laev.remem.repository.ReviewDatetimeRepository
import com.laev.remem.service.dto.ReviewItemDetails
import com.laev.remem.service.dto.ReviewItemMemorizationCount
import com.laev.remem.utils.CycleCalculator
import com.laev.remem.utils.DateTimeUtils
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
            val zoneOffset = request.offset.toZoneOffset()

            val newReviewItem = reviewItemRepository.save(
                ReviewItem(
                    mainText = request.mainText,
                    subText = request.subText,
                    member = member,
                )
            )

            reviewDatetimeComponent.createReviewDatetimeCycle(zoneOffset, newReviewItem)
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
        val item = reviewItemRepository.getReferenceById(itemId)

        // avoid duplicated row insertion
        val duplicatedReviewDatetime = reviewDatetimeRepository.findByStartAndItemId(startDatetime, itemId)
        if (duplicatedReviewDatetime.isEmpty()) {
            reviewDatetimeRepository.save(
                ReviewDatetime(
                    start = startDatetime,
                    reviewItem = item,
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
