package com.homelab.nosqldemo

import com.homelab.nosqldemo.book.domain.BookMother
import com.homelab.nosqldemo.book.infrastructure.DynamoDBRepository
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DynamoDBRepositoryTest {

    private val dynamoDBRepository = DynamoDBRepository()

    @BeforeEach
    fun setUp() {
        dynamoDBRepository.cleanUp()
    }

    @Test
    fun `save and finds a book`() {
        val book = BookMother.random()

        dynamoDBRepository.save(book)
        val foundBook = dynamoDBRepository.find(book.id)

        assertEquals(book, foundBook)
    }

    @Test
    fun `finds all books`() {
        val books = (1..20).map { BookMother.random() }
        books.forEach { dynamoDBRepository.save(it) }

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
    fun `inserts 1_000 books in DynamoDb`() {
        val books = (1..1_000).map { BookMother.random() }

        books.forEach { dynamoDBRepository.save(it) }
    }
}