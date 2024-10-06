package com.laev.reminder.controller

import com.laev.reminder.dto.GetItemsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/items")
class ItemController {

    @GetMapping
    @Operation(summary = "Get items", description = "Fetch all items or items for a specific date.")
    fun getItems(
        @RequestParam(required = false)
        @Parameter(description = "ISO datetime", example = "2024-10-05T22:09:23.648Z")
        date: String?
    ): GetItemsResponse {

        return GetItemsResponse(
            id = "1",
            mainText = "Banana",
            subText = "바나나",
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
}