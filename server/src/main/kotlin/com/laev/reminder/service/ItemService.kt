package com.laev.reminder.service

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.entity.Item
import com.laev.reminder.entity.Member
import com.laev.reminder.exception.ItemCreationException
import com.laev.reminder.repository.ItemRepository
import com.laev.reminder.utils.CycleCalculator
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {
    fun getItems(): List<Item> = itemRepository.findAll()

    fun addItem(request: AddItemRequest) {
        try {
            val member = Member(1, "Lyla") // TODO
            val createDatetime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS) // save time in UTC
            val cycles = listOf(1, 3, 7, 21)
            val reviewDates = CycleCalculator.getReviewDates(createDatetime, cycles)

            itemRepository.save(
                Item(
                    mainText = request.mainText,
                    subText = request.subText,
                    member = member,
                    reviewDates = reviewDates.toString(),
                )
            )
        } catch (e: DataIntegrityViolationException) {
            throw ItemCreationException("Failed to create item due to data integrity violation.")
        } catch (e: Exception) {
            throw ItemCreationException("An unexpected error occurred while creating the item.")
        }
    }
}