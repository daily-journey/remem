package com.laev.reminder.service

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.entity.Item
import com.laev.reminder.entity.Member
import com.laev.reminder.entity.MemorizationLog
import com.laev.reminder.entity.ReviewDatetime
import com.laev.reminder.exception.ItemCreationException
import com.laev.reminder.exception.ItemNotFoundException
import com.laev.reminder.repository.ItemRepository
import com.laev.reminder.repository.MemorizationLogRepository
import com.laev.reminder.repository.ReviewDatetimeRepository
import com.laev.reminder.utils.CycleCalculator
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
    private val memorizationLogRepository: MemorizationLogRepository,
) {
    fun getItems(datetime: OffsetDateTime?): List<Item> {
        if (datetime == null) {
            return itemRepository.findAll()
        }

        val reviewDatetimes = reviewDatetimeRepository.findByDatetimeRange(datetime)

        return reviewDatetimes.map { it.item }
    }

    @Transactional
    fun addItem(request: AddItemRequest) {
        try {
            val member = Member(1, "Lyla") // TODO
            val createDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS) // save time in UTC
            val cycles = listOf(1, 3, 7, 21)
            val reviewDates = CycleCalculator.getReviewDates(createDatetime, cycles)

            val zoneOffset = request.offset.toZoneOffset()
            val zonedCreatedDatetime = createDatetime.withOffsetSameInstant(zoneOffset) // Convert to local time with the applied zoneOffset

            val newItem = itemRepository.save(
                Item(
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
                        item = newItem,
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
        val item = itemRepository.findById(itemId).orElseThrow {
            ItemNotFoundException(itemId)
        }
        createMemorizationLog(isMemorized, item)

        // if not memorized, add a new review date for tomorrow
        if (!isMemorized) {
            createReviewDate(itemId, offset, 1)
        }
    }

    private fun createMemorizationLog(isMemorized: Boolean, item: Item) {
        val createdDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)

        memorizationLogRepository.save(
            MemorizationLog(
                isMemorized = isMemorized,
                createdDatetime = createdDatetime,
                item = item,
            )
        )
    }

    private fun createReviewDate(itemId: Long, offset: ZoneOffset, cycle: Int) {
        val zoneOffset = offset.toZoneOffset()
        val createdDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        val zonedCreatedDatetime = createdDatetime.withOffsetSameInstant(zoneOffset)
        val startDatetime = CycleCalculator.getUTCStartDatetime(zonedCreatedDatetime, cycle, zoneOffset)
        val endDatetime = startDatetime.plusHours(24)
        val item = itemRepository.getReferenceById(itemId)

        // avoid duplicated row insertion
        val duplicatedReviewDatetime = reviewDatetimeRepository.findByStartAndItemId(startDatetime, itemId)
        if (duplicatedReviewDatetime.isEmpty()) {
            reviewDatetimeRepository.save(
                ReviewDatetime(
                    start = startDatetime,
                    end = endDatetime,
                    item = item
                )
            )
        }
    }

    private fun ZoneOffset.toZoneOffset(): ZoneOffset {
        val offsetHours = this.totalSeconds / 3600 // Convert the offset to hours
        return ZoneOffset.ofHours(offsetHours)
    }
}