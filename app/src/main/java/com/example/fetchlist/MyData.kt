package com.example.fetchlist

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyData (
    val id: Int,
    val listId: Int,
    val name: String?
)

