package com.laev.reminder.service

import com.laev.reminder.entity.Item
import com.laev.reminder.repository.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {
    fun getItems(): List<Item> = itemRepository.findAll()
}