package com.homelab.nosqldemo.book.domain

import java.util.UUID

interface BookRepository {
    fun save(book: Book)
    fun find(bookId: UUID): Book?
    fun findAll(): List<Book>
    fun delete(bookId: UUID)
}