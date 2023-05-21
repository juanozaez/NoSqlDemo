package com.homelab.nosqldemo.book.infrastructure

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.homelab.nosqldemo.book.domain.Book
import com.homelab.nosqldemo.book.domain.BookRepository
import java.util.UUID

class DynamoDBRepository : BookRepository {
    private fun createDynamoDBConnection(): AmazonDynamoDBClient {
        val awsCredentials = BasicAWSCredentials("amazonAWSAccessKey", "amazonAWSSecretKey")
        return AmazonDynamoDBClient(awsCredentials).also { it.setEndpoint("http://localhost:8000"); }
    }

    override fun save(book: Book) {
        createDynamoDBConnection().putItem(PutItemRequest("Book", book.toItem()))
    }

    override fun find(bookId: UUID): Book? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Book> {
        TODO("Not yet implemented")
    }

    override fun delete(bookId: UUID) {
        TODO("Not yet implemented")
    }

    private fun Book.toItem(): MutableMap<String, AttributeValue> {
        return mutableMapOf(
            "id" to AttributeValue().withS(id.toString()),
            "title" to AttributeValue().withS(title),
            "author" to AttributeValue().withS(author),
            "genre" to AttributeValue().withS(genre),
            "year" to AttributeValue().withS(year.toString()),
            "price" to AttributeValue().withN(price.toString())
        )
    }
}