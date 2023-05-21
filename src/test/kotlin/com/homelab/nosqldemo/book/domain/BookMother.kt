package com.homelab.nosqldemo.book.domain

import io.github.serpro69.kfaker.Faker
import java.util.UUID

object BookMother {

    private val faker: Faker = Faker()

    fun random() = Book(
        id = UUID.randomUUID(),
        title = faker.book.title(),
        author = faker.book.author(),
        year = (1900..2020).random(),
        genre = faker.book.genre(),
        price = (5..50).random().toDouble()
    )
}