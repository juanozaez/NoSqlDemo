package com.homelab.nosqldemo

import com.homelab.nosqldemo.book.domain.BookMother
import com.homelab.nosqldemo.book.infrastructure.RedisRepository
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class RedisRepositoryTest {

    private val redisRepository = RedisRepository()

    @Test
    fun `save and finds a book`() {
        val book = BookMother.random()

        redisRepository.save(book)
        val foundBook = redisRepository.find(book.id)

        assertEquals(book, foundBook)
    }

    @Test
    fun `finds all books`() {
        val books = (1..20).map { BookMother.random() }
        books.forEach { redisRepository.save(it) }

        val list = redisRepository.findAll()

        books shouldContainAll list
    }

    @Test
    fun `deletes a book`() {
        val book = BookMother.random()
        redisRepository.save(book)

        redisRepository.delete(book.id)

        redisRepository.find(book.id) shouldBe null
    }

    @Test
    fun `inserts 1_000 books`() {
        val books = (1..1_000).map { BookMother.random() }

        books.forEach { redisRepository.save(it) }
    }
}