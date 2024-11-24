package com.laev.reminder.service

import com.laev.reminder.entity.Member
import com.laev.reminder.exception.EmailAlreadyExistsException
import com.laev.reminder.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val memberRepository: MemberRepository,
) {
    fun signUp(email: String, password: String, name: String) {
        if (isDuplicatedEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }
        val newMember = Member(
            email = email,
            password = password,
            name = name,
        )
        memberRepository.save(newMember)
    }

    private fun isDuplicatedEmail(email: String): Boolean {
        return memberRepository.findByEmail(email) != null
    }
}