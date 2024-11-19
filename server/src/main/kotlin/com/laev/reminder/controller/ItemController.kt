package com.laev.reminder.controller

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.dto.GetItemsResponse
import com.laev.reminder.dto.UpdateMemorizationRequest
import com.laev.reminder.exception.ItemNotFoundException
import com.laev.reminder.service.ItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime

@RestController
@RequestMapping("/items")
class ItemController(
    private val itemService: ItemService,
) {
    @GetMapping
    @Operation(summary = "Get items", description = "Fetch all items or items for a specific date.")
    fun getItems(
        @RequestParam(required = false)
        @Parameter(description = "ISO datetime", example = "2024-11-18T05:00:00Z")
        datetime: OffsetDateTime?
    ): ResponseEntity<List<GetItemsResponse>> {
        val responseHeaders = HttpHeaders()
        responseHeaders.set("Content-Type", "application/json")

        val items = itemService.getItems(datetime)

        return ResponseEntity.ok()
            .headers(responseHeaders)
            .body(
                items.map { item ->
                    GetItemsResponse(
                        id = item.id ?: 0,
                        mainText = item.mainText,
                        subText = item.subText,
                        createdDatetime = item.createdDatetime,
                        successCount = item.successCount,
                        failCount = item.failCount,
                        isRecurring = item.isRecurring,
                        reviewDates = item.reviewDates
                            .removePrefix("[").removeSuffix("]")
                            .split(", "),
                    )
                }
            )
    }

    @PostMapping
    @Operation(summary = "Add an item")
    fun addItem(
        @RequestBody @Valid request: AddItemRequest,
    ): ResponseEntity<Void> {
        itemService.addItem(request)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PatchMapping("/{id}/memorization")
    @Operation(summary = "Mark an item as memorized or not")
    fun updateMemorization(
        @RequestBody @Valid request: UpdateMemorizationRequest,
        @PathVariable @NotNull(message = "ID cannot be null") id: Long,
    ): ResponseEntity<String?> {
        try {
            itemService.updateMemorization(id, request.isMemorized!!, request.offset)
        } catch (e: ItemNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
