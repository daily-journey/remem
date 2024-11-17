package com.laev.reminder.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.entity.Item
import com.laev.reminder.entity.Member
import com.laev.reminder.service.ItemService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.OffsetDateTime

@WebMvcTest(ItemController::class)
class ItemControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var itemService: ItemService

    private val objectMapper = ObjectMapper()

    @Test
    fun `should fetch all items`() {
        val mockItems = listOf(
            Item(
                id = 1,
                mainText = "Test Main Text",
                subText = "Test Sub Text",
                createDatetime = OffsetDateTime.now(),
                successCount = 3,
                failCount = 2,
                isRecurring = false,
                reviewDates = "[2024-11-14T15:17:27Z, 2024-11-16T15:17:27Z, 2024-11-20T15:17:27Z, 2024-12-04T15:17:27Z]",
                member = Member(1, "Lyla")
            )
        )

        every { itemService.getItems() } returns mockItems

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].mainText").value("Test Main Text"))
    }

    @Test
    fun `should add item`() {
        val request = AddItemRequest(
            mainText = "Test Item",
            subText = "Sub Text",
        )

        every { itemService.addItem(any()) } returns Unit

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}