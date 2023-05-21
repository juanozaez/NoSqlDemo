package com.homelab.nosqldemo.book.domain

import com.homelab.nosqldemo.book.infrastructure.DynamoDBRepository
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.ZonedDateTime

class DynamoDBRepositoryTest {

    private val dynamoDBRepository = DynamoDBRepository()

    @Test
    fun `save and finds a book`() {
        val book = BookMother.random()

        dynamoDBRepository.save(book)
        val foundBook = dynamoDBRepository.find(book.id)

        assertEquals(book, foundBook)
    }

    @Test
    fun `finds all books`() {
        val books = listOf(BookMother.random())
        //books.forEach { dynamoDBRepository.save(it) }

        val list = dynamoDBRepository.findAll()

        books shouldContainAll list
    }

    @Test
    fun `deletes a book`() {
        val book = BookMother.random()
        dynamoDBRepository.save(book)

        dynamoDBRepository.delete(book.id)

        dynamoDBRepository.find(book.id) shouldBe null
    }

    @Test
    fun `inserts 10_000 books`() {
        val books = (1..100_000).map { BookMother.random() }

        println("Start")
        println(ZonedDateTime.now())
        books.forEach { dynamoDBRepository.save(it) }
        println("End")
        println(ZonedDateTime.now())
    }
}