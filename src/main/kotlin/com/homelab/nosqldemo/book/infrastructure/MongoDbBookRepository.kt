package com.homelab.nosqldemo.book.infrastructure

import com.homelab.nosqldemo.book.domain.Book
import com.homelab.nosqldemo.book.domain.BookRepository
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import java.util.UUID
import org.bson.Document
import org.bson.UuidRepresentation
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.codecs.pojo.annotations.BsonId

class MongoDbBookRepository : BookRepository {
    private val collection = createCollection()

    private fun createCollection(): MongoCollection<MongoDbBook> {
        val pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build()
        val codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(pojoCodecProvider)
        )
        val client = MongoClients.create(
            MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD).codecRegistry(codecRegistry)
                .applyConnectionString(ConnectionString("mongodb://localhost:27017")).build()
        )
        val database = client.getDatabase("library")
        return database.getCollection("books", MongoDbBook::class.java)
    }

    override fun save(book: Book) {
        collection.insertOne(book.toMongoDbBook())
    }

    override fun find(bookId: UUID): Book? {
        return collection.find(Document("_id", bookId)).firstOrNull()?.toBook()
    }

    override fun findAll(): List<Book> {
        return collection.find().toList().map { it.toBook() }
    }

    override fun delete(bookId: UUID) {
        collection.deleteOne(Document("_id", bookId))
    }

    fun cleanUp() {
        collection.deleteMany(Document())
    }

    private fun Book.toMongoDbBook() = MongoDbBook(id, title, author, year, genre, price)
    private fun MongoDbBook.toBook() = Book(id!!, title!!, author!!, year!!, genre!!, price!!)
}

data class MongoDbBook(
    @BsonId var id: UUID? = null,
    var title: String? = null,
    var author: String? = null,
    var year: Int? = null,
    var genre: String? = null,
    var price: Double? = null,
)

