package com.laev.remem.integration

import com.laev.remem.dto.AddItemRequest
import com.laev.remem.repository.ReviewDatetimeRepository
import com.laev.remem.utils.ObjectMapperUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReviewItemTest(
    @Autowired private val reviewDatetimeRepository: ReviewDatetimeRepository,
) : BaseIntegrationTest() {
    private val objectMapper = ObjectMapperUtil.createObjectMapper()

    @AfterEach
    fun tearDown() {
        reviewDatetimeRepository.deleteAll()
        reviewItemRepository.deleteAll()
    }

    @Test
    fun `save item to database`() {
        val request = AddItemRequest(
            mainText = "TestCode Item",
            subText = "Sub Text",
            offset = ZoneOffset.of("-05:00"),
        )

        mockMvc.perform(
            withAuth(
                MockMvcRequestBuilders.post("/review-items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
        ).andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `fetch items from database`() {
        mockMvc.perform(
            withAuth(
                MockMvcRequestBuilders.get("/review-items")
                    .accept(MediaType.APPLICATION_JSON)
            )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            // Check the type and presence of each field
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].mainText").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].subText").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdDatetime").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].reviewDates").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].isRecurring").isBoolean)
    }

    @Transactional
    @Test
    fun `delete item successfully updates is_deleted column`() {
        val item = reviewItemRepository.findAll().first()

        mockMvc.perform(
            withAuth(
                MockMvcRequestBuilders.delete("/review-items/${item.id}")
                    .accept(MediaType.APPLICATION_JSON)
            )
        ).andExpect(MockMvcResultMatchers.status().isOk)

        val updatedItem = reviewItemRepository.findById(item.id).orElseThrow()
        assert(updatedItem.isDeleted) { "Item should be marked as deleted" }
    }

    @Test
    fun `delete item with non-existent id returns 404`() {
        val nonExistentId = 9999L

        mockMvc.perform(
            withAuth(
                MockMvcRequestBuilders.delete("/review-items/$nonExistentId")
                    .accept(MediaType.APPLICATION_JSON)
            )
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.content().string("Item with ID $nonExistentId not found"))
    }

    @Test
    fun `delete item already marked as deleted returns error`() {
        val item = reviewItemRepository.findAll().first()
        item.isDeleted = true
        reviewItemRepository.save(item)

        mockMvc.perform(
            withAuth(
                MockMvcRequestBuilders.delete("/review-items/${item.id}")
                    .accept(MediaType.APPLICATION_JSON)
            )
        ).andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.content().string("Item is already deleted."))
    }
}
