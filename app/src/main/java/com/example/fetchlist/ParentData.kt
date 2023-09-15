package com.example.fetchlist

data class ParentData (
    val listId: Int,
    var subList: List<MyData> = emptyList(),
//    var isExpandable: Boolean = true
)
