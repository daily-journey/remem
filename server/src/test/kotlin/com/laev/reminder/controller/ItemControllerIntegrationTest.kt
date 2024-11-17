package com.laev.reminder.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.entity.Member
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerIntegrationTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val entityManager: EntityManager,
) {
    @BeforeEach
    fun setup() {
//        // Add a member to the database
//        val member = Member(
//            id = null, // Let the database auto-generate the ID
//            name = "Lyla"
//        )
//        entityManager.persist(member) // Persist the member to the database
//        entityManager.flush() // Ensure changes are persisted

        val request = AddItemRequest(
            mainText = "TestCode Item",
            subText = "Sub Text",
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `save item to database`() {
        val request = AddItemRequest(
            mainText = "TestCode Item",
            subText = "Sub Text",
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `fetch items from database`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            // Check the type and presence of each field
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].mainText").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].subText").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createDatetime").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].successCount").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].failCount").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].reviewDates").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].isRecurring").isBoolean)
    }

    @Test
    fun `fetch specific date's items from database`() {
        // Get tomorrow's date in UTC with ISO-8601 format
        val tomorrowUtc = OffsetDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        mockMvc.perform(MockMvcRequestBuilders.get("/items") // TODO add param
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].reviewDates").value(Matchers.hasItem(tomorrowUtc))) // Check if tomorrow is included
    }

    @Test
    fun `should update item to database`() {
        val request = AddItemRequest(
            mainText = "TestCode Item",
            subText = "Sub Text",
        )

        mockMvc.perform(MockMvcRequestBuilders.patch("/item")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}