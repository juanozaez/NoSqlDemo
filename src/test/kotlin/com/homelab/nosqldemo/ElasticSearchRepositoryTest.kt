package com.homelab.nosqldemo

import com.homelab.nosqldemo.book.domain.BookMother
import com.homelab.nosqldemo.book.infrastructure.ElasticSearchRepository
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

class ElasticSearchRepositoryTest {

    private val repository = ElasticSearchRepository()

    @BeforeEach
    fun setUp() {
        repository.cleanUp()
    }

    @Test
    fun `save and finds a book`() {
        val book = BookMother.random()

        repository.save(book)
        val foundBook = repository.find(book.id)

        assertEquals(book, foundBook)
    }

    @Test
    fun `finds all books`() {
        val books = (1..20).map { BookMother.random() }
        books.forEach { repository.save(it) }

        val list = repository.findAll()

        books shouldContainAll list
    }

    @Test
    fun `deletes a book`() {
        val book = BookMother.random()
        repository.save(book)

        repository.delete(book.id)

        repository.find(book.id) shouldBe null
    }

    @Test
    fun `inserts 1_000 books`() {
        val books = (1..1_000).map { BookMother.random() }

        books.forEach { repository.save(it) }
    }
}