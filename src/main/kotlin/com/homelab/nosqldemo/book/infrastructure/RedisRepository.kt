package com.homelab.nosqldemo.book.infrastructure

import com.homelab.nosqldemo.book.domain.Book
import com.homelab.nosqldemo.book.domain.BookRepository
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.util.UUID

class RedisRepository : BookRepository {

    private val connection: Jedis = createRedisConnection()

    override fun save(book: Book) {
        connection.hset(book.id.toString(), book.asMap())
    }

    override fun find(bookId: UUID): Book? =
        connection.hgetAll(bookId.toString()).takeIf { it.isNotEmpty() }?.toBook()

    override fun findAll(): List<Book> {
        //return connection.keys("*").map { Book(UUID.fromString(it), connection.get(it)) }
        return emptyList()
    }

    override fun delete(bookId: UUID) {
        connection.del(bookId.toString())
    }

    private fun createRedisConnection() =
        JedisPool("localhost", 6379).resource

    private fun Book.asMap() = mapOf(
        "id" to id.toString(),
        "title" to title,
        "author" to author,
        "year" to year.toString(),
        "genre" to genre,
        "price" to price.toString()
    )

    private fun Map<String, String>.toBook() = Book(
        UUID.fromString(this["id"]),
        title = this["title"]!!,
        author = this["author"]!!,
        year = this["year"]!!.toInt(),
        genre = this["genre"]!!,
        price = this["price"]!!.toDouble()
    )
}