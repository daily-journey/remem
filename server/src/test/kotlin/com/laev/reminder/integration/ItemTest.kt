package com.laev.reminder.integration

import com.laev.reminder.dto.AddItemRequest
import com.laev.reminder.entity.Item
import com.laev.reminder.entity.Member
import com.laev.reminder.repository.ItemRepository
import com.laev.reminder.repository.MemberRepository
import com.laev.reminder.utils.ObjectMapperUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.ZoneOffset

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.yml"])
@AutoConfigureMockMvc
class ItemTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val itemRepository: ItemRepository,
) {
    private val objectMapper = ObjectMapperUtil.createObjectMapper()

    @BeforeEach
    fun setUp() {
        val testMember = Member(email = "test@example.com", password = "0000", name = "Lyla")
        memberRepository.save(testMember)
        itemRepository.save(
            Item(
                mainText = "setup item",
                subText = "",
                reviewDates = "",
                member = testMember,
            )
        )
    }

    @Test
    fun `save item to database`() {
        val request = AddItemRequest(
            mainText = "TestCode Item",
            subText = "Sub Text",
            offset = ZoneOffset.of("-05:00"),
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
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
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdDatetime").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].successCount").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].failCount").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].reviewDates").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].isRecurring").isBoolean)
    }
}