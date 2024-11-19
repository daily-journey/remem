package com.laev.reminder.exception

class ItemNotFoundException(itemId: Long): RuntimeException("Item with ID $itemId not found")