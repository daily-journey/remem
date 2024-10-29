package com.laev.reminder.service

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.entity.Item
import com.laev.reminder.entity.Member
import com.laev.reminder.exception.ItemCreationException
import com.laev.reminder.repository.ItemRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {
    fun getItems(): List<Item> = itemRepository.findAll()

    fun addItem(request: AddItemRequest) {
        try {
            val member = Member(1, "Lyla") // TODO

            itemRepository.save(
                Item(
                    mainText = request.mainText,
                    subText = request.subText,
                    createDatetime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    successCount = 0,
                    failCount = 0,
                    isRecurring = true,
                    member = member,
                    periods = "",
                )
            )
        } catch (e: DataIntegrityViolationException) {
            throw ItemCreationException("Failed to create item due to data integrity violation.")
        } catch (e: Exception) {
            throw ItemCreationException("An unexpected error occurred while creating the item.")
        }
    }
}