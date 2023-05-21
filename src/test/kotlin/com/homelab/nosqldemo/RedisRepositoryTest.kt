package com.homelab.nosqldemo

import com.homelab.nosqldemo.book.domain.BookMother
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
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
        val books = listOf(BookMother.random())
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
    fun `inserts 10_000 books`() {
        val books = (1..10_000).map { BookMother.random() }

        println("Start")
        println(ZonedDateTime.now())
        books.forEach { redisRepository.save(it) }
        println("End")
        println(ZonedDateTime.now())

    }
}