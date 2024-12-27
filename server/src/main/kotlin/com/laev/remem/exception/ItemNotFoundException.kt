package com.laev.remem.exception

class ItemNotFoundException(itemId: Long): RuntimeException("Item with ID $itemId not found")
