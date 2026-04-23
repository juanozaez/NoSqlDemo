package com.homelab.nosqldemo.book.infrastructure

import com.homelab.nosqldemo.book.domain.Book
import com.homelab.nosqldemo.book.domain.BookRepository
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.UUID

class MySqlBookRepository : BookRepository {

    private val connection: Connection by lazy {
        DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/database",
            "user",
            "password"
        ).also {
            createTableIfNotExists(it)
        }
    }

    private fun createTableIfNotExists(conn: Connection) {
        val sql = """
            CREATE TABLE IF NOT EXISTS books (
                id VARCHAR(36) PRIMARY KEY,
                title VARCHAR(255),
                author VARCHAR(255),
                year INT,
                genre VARCHAR(255),
                price DOUBLE
            )
        """.trimIndent()
        conn.createStatement().use { it.execute(sql) }
    }

    override fun save(book: Book) {
        val sql = """
            INSERT INTO books (id, title, author, year, genre, price)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            title = VALUES(title),
            author = VALUES(author),
            year = VALUES(year),
            genre = VALUES(genre),
            price = VALUES(price)
        """.trimIndent()
        
        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, book.id.toString())
            stmt.setString(2, book.title)
            stmt.setString(3, book.author)
            stmt.setInt(4, book.year)
            stmt.setString(5, book.genre)
            stmt.setDouble(6, book.price)
            stmt.executeUpdate()
        }
    }

    override fun find(bookId: UUID): Book? {
        val sql = "SELECT * FROM books WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, bookId.toString())
            stmt.executeQuery().use { rs ->
                return if (rs.next()) {
                    rs.toBook()
                } else {
                    null
                }
            }
        }
    }

    override fun findAll(): List<Book> {
        val sql = "SELECT * FROM books"
        connection.createStatement().use { stmt ->
            stmt.executeQuery(sql).use { rs ->
                val books = mutableListOf<Book>()
                while (rs.next()) {
                    books.add(rs.toBook())
                }
                return books
            }
        }
    }

    override fun delete(bookId: UUID) {
        val sql = "DELETE FROM books WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, bookId.toString())
            stmt.executeUpdate()
        }
    }

    private fun ResultSet.toBook(): Book {
        return Book(
            id = UUID.fromString(getString("id")),
            title = getString("title"),
            author = getString("author"),
            year = getInt("year"),
            genre = getString("genre"),
            price = getDouble("price")
        )
    }

    fun cleanUp() {
        val sql = "DELETE FROM books"
        connection.createStatement().use { it.execute(sql) }
    }
}
