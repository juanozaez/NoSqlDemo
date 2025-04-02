package com.homelab.nosqldemo.book.infrastructure

import com.homelab.nosqldemo.book.domain.Book
import com.homelab.nosqldemo.book.domain.BookRepository
import org.apache.http.HttpHost
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.reindex.DeleteByQueryRequest
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.xcontent.XContentType
import java.util.UUID

class ElasticSearchRepository : BookRepository {

    private val connection = createConnection()
    override fun save(book: Book) {
        IndexRequest("books")
            .id(book.id.toString())
            .source(
                mapOf(
                    "title" to book.title,
                    "author" to book.author,
                    "year" to book.year,
                    "genre" to book.genre,
                    "price" to book.price
                ),
                XContentType.JSON
            ).run {
                connection.index(this, RequestOptions.DEFAULT)
            }
    }

    override fun find(bookId: UUID): Book? {
        val getResponse = connection.get(GetRequest("books", bookId.toString()), RequestOptions.DEFAULT)
        return takeIf { getResponse.isExists }
            ?.let {
                Book(
                    UUID.fromString(getResponse.id),
                    getResponse.sourceAsMap["title"] as String,
                    getResponse.sourceAsMap["author"] as String,
                    getResponse.sourceAsMap["year"] as Int,
                    getResponse.sourceAsMap["genre"] as String,
                    getResponse.sourceAsMap["price"] as Double
                )
            }
    }

    override fun findAll(): List<Book> {
        val searchRequest = SearchRequest("books")
        searchRequest.source(SearchSourceBuilder().query(QueryBuilders.matchAllQuery()))
        val searchResponse = connection.search(searchRequest, RequestOptions.DEFAULT)
        return searchResponse.hits.map {
            Book(
                UUID.fromString(it.id),
                it.sourceAsMap["title"] as String,
                it.sourceAsMap["author"] as String,
                it.sourceAsMap["year"] as Int,
                it.sourceAsMap["genre"] as String,
                it.sourceAsMap["price"] as Double
            )
        }
    }



    override fun delete(bookId: UUID) {
        connection.delete(DeleteRequest("books", bookId.toString()), RequestOptions.DEFAULT)
    }

    fun cleanUp() {
        connection.deleteByQuery(
            DeleteByQueryRequest("books")
                .setQuery(QueryBuilders.matchAllQuery()), RequestOptions.DEFAULT
        )
    }

    private fun createConnection() = RestHighLevelClient(RestClient.builder(HttpHost.create("localhost:9200")))
}
