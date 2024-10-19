package com.laev.reminder.controller

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.dto.GetItemsResponse
import com.laev.reminder.service.ItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/items")
class ItemController(
    private val itemService: ItemService,
) {
    @GetMapping
    @Operation(summary = "Get items", description = "Fetch all items or items for a specific date.")
    fun getItems(
        @RequestParam(required = false)
        @Parameter(description = "ISO datetime", example = "2024-10-05T22:09:23.648Z")
        date: String?
    ): ResponseEntity<List<GetItemsResponse>> {
        val responseHeaders = HttpHeaders()
        responseHeaders.set("Content-Type", "application/json")

        val items = itemService.getItems()

        return ResponseEntity.ok()
            .headers(responseHeaders)
            .body(
                items.map { item ->
                    GetItemsResponse(
                        id = item.id ?: 0,
                        mainText = item.mainText,
                        subText = item.subText,
                        createDatetime = "2024-10-05T22:09:23.648Z",
                        successCount = 0,
                        failCount = 0,
                        isRepeated = true,
                        nextRemindDatetimes = listOf(
                            "2024-10-05T22:09:23.648Z",
                            "2024-10-12T22:09:23.648Z"
                        )
                    )
                }
            )
    }

    @PostMapping
    @Operation(summary = "Add an item")
    fun addItem(
        @Valid @RequestBody request: AddItemRequest,
    ): ResponseEntity<Void> {
        itemService.addItem(request)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
