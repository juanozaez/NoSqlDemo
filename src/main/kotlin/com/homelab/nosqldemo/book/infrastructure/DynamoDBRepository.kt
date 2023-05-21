package com.homelab.nosqldemo.book.infrastructure

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.homelab.nosqldemo.book.domain.Book
import com.homelab.nosqldemo.book.domain.BookRepository
import java.util.UUID

class DynamoDBRepository : BookRepository {

    private val connection = dynamoDbClient()
    private fun dynamoDbClient() =
        AmazonDynamoDBClientBuilder.standard()
            .withClientConfiguration(ClientConfiguration())
            .withCredentials(DefaultAWSCredentialsProviderChain())
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:8000/", "local"))
        .build()

    override fun save(book: Book) {
        connection.createTable(CreateTableRequest("Book", listOf()))
        connection.putItem(PutItemRequest("Book", book.toItem()))
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