package com.laev.reminder.service

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.entity.Item
import com.laev.reminder.entity.Member
import com.laev.reminder.exception.ItemCreationException
import com.laev.reminder.repository.ItemRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {
    fun getItems(): List<Item> = itemRepository.findAll()

    fun addItem(request: AddItemRequest) {
        try {
            val member = Member(1, "Lyla")

            itemRepository.save(
                Item(
                    mainText = request.mainText,
                    subText = request.subText,
                    member = member,
                )
            )
        } catch (e: DataIntegrityViolationException) {
            throw ItemCreationException("Failed to create item due to data integrity violation.")
        } catch (e: Exception) {
            throw ItemCreationException("An unexpected error occurred while creating the item.")
        }
    }
}