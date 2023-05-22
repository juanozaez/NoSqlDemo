package com.homelab.nosqldemo.book.infrastructure

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.homelab.nosqldemo.book.domain.Book
import com.homelab.nosqldemo.book.domain.BookRepository
import java.util.UUID


class DynamoDBRepository : BookRepository {

    private val connection = dynamoDbClient()


    private fun dynamoDbClient() =
        AmazonDynamoDBClientBuilder.standard()
            .withClientConfiguration(ClientConfiguration())
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("local", "local")))
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:8000/", "local"))
            .build()

    override fun save(book: Book) {
        createTableIfNotExists()
        connection.putItem(PutItemRequest("book", book.toItem()))
    }

    override fun find(bookId: UUID): Book? {
        val item = connection.getItem("book", mapOf("id" to AttributeValue().withS(bookId.toString())))
        return item.item?.let { it.toBook() }
    }

    private fun Map<String, AttributeValue>.toBook() =
        Book(
            UUID.fromString(this["id"]?.s),
            this["title"]!!.s,
            this["author"]!!.s,
            this["year"]!!.n.toInt(),
            this["genre"]!!.s,
            this["price"]!!.n.toDouble()
        )

    override fun findAll(): List<Book> {
        val scan = connection.scan("book", listOf("id", "title", "author", "year", "genre", "price"))
        return scan.items.map { it.toBook() }
    }

    override fun delete(bookId: UUID) {
        connection.deleteItem("book", mapOf("id" to AttributeValue().withS(bookId.toString())))
    }

    private fun Book.toItem(): MutableMap<String, AttributeValue> {
        return mutableMapOf(
            "id" to AttributeValue().withS(id.toString()),
            "title" to AttributeValue().withS(title),
            "author" to AttributeValue().withS(author),
            "genre" to AttributeValue().withS(genre),
            "year" to AttributeValue().withN(year.toString()),
            "price" to AttributeValue().withN(price.toString())
        )
    }

    private fun createTableIfNotExists() {
        takeIf { (connection.listTables().tableNames.isEmpty()) }
            ?.let { createTable() }
    }

    private fun createTable() {
        val request = CreateTableRequest()
            .withAttributeDefinitions(
                AttributeDefinition("id", ScalarAttributeType.S),
            )
            .withKeySchema(KeySchemaElement("id", KeyType.HASH))
            .withProvisionedThroughput(ProvisionedThroughput(10, 10))
            .withTableName("book")
        connection.createTable(request)
    }
}