package com.laev.remem.controller

import com.laev.remem.dto.*
import com.laev.remem.service.AuthService
import com.laev.remem.service.ReviewDatetimeService
import com.laev.remem.service.ReviewItemService
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
@RequestMapping("/review-items")
class ReviewItemController(
    private val authService: AuthService,
    private val reviewItemService: ReviewItemService,
    private val reviewDatetimeService: ReviewDatetimeService,
) {
    @GetMapping
    @Operation(summary = "Get items", description = "Fetch all items or items for a specific date.")
    fun getItems(
        @RequestHeader("Authorization")
        authorizationHeader: String,
        @RequestParam(required = false)
        @Parameter(description = "ISO datetime", example = "2024-11-18T05:00:00Z")
        datetime: OffsetDateTime?
    ): ResponseEntity<List<GetItemsResponse>> {
        val responseHeaders = HttpHeaders()
        responseHeaders.set("Content-Type", "application/json")

        val member = authService.getMemberFromToken(authorizationHeader)
        val items = reviewItemService.getReviewItems(datetime, member)

        return ResponseEntity.ok()
            .headers(responseHeaders)
            .body(
                items.map { item ->
                    val reviewDates = reviewDatetimeService.getReviewDatetimes(item.id)
                    GetItemsResponse(
                        id = item.id,
                        mainText = item.mainText,
                        subText = item.subText,
                        createdDatetime = item.createdDatetime,
                        isRecurring = item.isRecurring,
                        reviewDates = reviewDates,
                    )
                }
            )
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's review items")
    fun getReviewItemsOfToday(
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<List<GetReviewItemsTodayResponse>>{
        val member = authService.getMemberFromToken(authorizationHeader)
        val items = reviewItemService.getReviewItemsOfToday(member)

        return ResponseEntity.ok().body(
            items.map { item ->
                GetReviewItemsTodayResponse(
                    id = item.id,
                    mainText = item.mainText,
                    status = item.status,
                )
            }
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item details")
    fun getItemDetails(
        @PathVariable @NotNull(message = "Item id cannot be null") id: Long,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<GetItemDetailsResponse> {
        val member = authService.getMemberFromToken(authorizationHeader)
        val reviewItemDetail = reviewItemService.getReviewItemDetail(member, id)

        return ResponseEntity.ok().body(
            GetItemDetailsResponse(
                id = reviewItemDetail.id,
                mainText = reviewItemDetail.mainText,
                subText = reviewItemDetail.subText,
                isRecurring = reviewItemDetail.isRecurring,
                upcomingReviewDates = reviewItemDetail.upcomingReviewDates,
                notMemorizedDates = reviewItemDetail.notMemorizedDates,
                memorizedDates = reviewItemDetail.memorizedDates,
                skippedDates = reviewItemDetail.skippedDates,
            )
        )
    }

    @PostMapping
    @Operation(summary = "Add an item")
    fun addItem(
        @RequestBody @Valid request: AddItemRequest,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<Void> {
        val member = authService.getMemberFromToken(authorizationHeader)
        reviewItemService.addReviewItem(request, member)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PatchMapping("/{id}/memorization")
    @Operation(summary = "Mark an item as memorized or not")
    fun updateMemorization(
        @RequestBody @Valid request: UpdateMemorizationRequest,
        @PathVariable @NotNull(message = "Item id cannot be null") id: Long,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<Void> {
        val member = authService.getMemberFromToken(authorizationHeader)
        reviewItemService.updateMemorization(member, id, request.isMemorized!!, request.offset)

        if (!request.isMemorized) {
            reviewDatetimeService.createNewReviewCycle(id, request.offset)
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item")
    fun deleteItem(
        @PathVariable @NotNull(message = "Item id cannot be null") id: Long,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<String> {
        val member = authService.getMemberFromToken(authorizationHeader)
        reviewItemService.deleteReviewItem(member, id)

        return ResponseEntity.ok("Item deleted successfully.")
    }
}
