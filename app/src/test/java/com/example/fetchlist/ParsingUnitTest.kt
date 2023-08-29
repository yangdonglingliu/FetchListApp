package com.example.fetchlist

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain

import org.junit.Before
import org.junit.Test
import org.junit.After
import org.junit.Assert

class ParsingUnitTest {

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("Test thread")

    private lateinit var viewModel: MyViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {

        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = MyViewModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun shutdown() {

        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun parse_empty_string(){

        val fetchedData = ""
        val parsedData = viewModel.parseJsonData(fetchedData)

        Assert.assertNull(parsedData)
    }

    @Test
    fun parse_filter_empty_item_to_null(){

        val fetchedData = "[{\"id\": 755, \"listId\": 2, \"name\": \"\"}]"
        val parsedData = viewModel.parseJsonData(fetchedData)

        Assert.assertNull(parsedData)
    }

    @Test
    fun parse_filter_empty_item(){

        val fetchedData = "[{\"id\": 755, \"listId\": 2, \"name\": \"\"},{\"id\": 684, \"listId\": 1, \"name\": \"Item 684\"}]"
        val parsedData = viewModel.parseJsonData(fetchedData)

        val expectedParsedData = listOf(ParentData(1,listOf(MyData(684, 1, "Item 684")), true))
        Assert.assertEquals(expectedParsedData, parsedData)
    }

    @Test
    fun parse_filter_null_item_to_null(){

        val fetchedData = "[{\"id\": 926, \"listId\": 4, \"name\": null}]"
        val parsedData = viewModel.parseJsonData(fetchedData)

        Assert.assertNull(parsedData)
    }

    @Test
    fun parse_filter_null_item(){

        val fetchedData = "[{\"id\": 926, \"listId\": 4, \"name\": null},{\"id\": 684, \"listId\": 1, \"name\": \"Item 684\"}]"
        val parsedData = viewModel.parseJsonData(fetchedData)

        val expectedParsedData = listOf(ParentData(1,listOf(MyData(684, 1, "Item 684")), true))
        Assert.assertEquals(expectedParsedData, parsedData)
    }

    @Test
    fun parse_filter_sort_listId(){

        val fetchedData = "[{\"id\": 808, \"listId\": 4, \"name\": \"Item 808\"},{\"id\": 684, \"listId\": 1, \"name\": \"Item 684\"}]"
        val parsedData = viewModel.parseJsonData(fetchedData)

        val expectedParsedData = listOf(ParentData(1,listOf(MyData(684, 1, "Item 684")), true),
                                        ParentData(4, listOf(MyData(808, 4, "Item 808")), true))
        Assert.assertEquals(expectedParsedData, parsedData)
    }

    @Test
    fun parse_filter_sort_name(){

        val fetchedData = "[{\"id\": 684, \"listId\": 1, \"name\": \"Item 684\"},{\"id\": 68, \"listId\": 1, \"name\": \"Item 68\"},{\"id\": 276, \"listId\": 1, \"name\": \"Item 276\"}]"
        val parsedData = viewModel.parseJsonData(fetchedData)

        val expectedParsedData = listOf(ParentData(1,listOf(MyData(68, 1, "Item 68"),
                                                                MyData(276, 1, "Item 276"),
                                                                MyData(684, 1, "Item 684")), true))
        Assert.assertEquals(expectedParsedData, parsedData)
    }

    @Test
    fun parse_all(){

        val fetchedData = "[{\"id\": 755, \"listId\": 2, \"name\": \"\"},{\"id\": 203, \"listId\": 2, \"name\": \"\"},{\"id\": 684, \"listId\": 1, \"name\": \"Item 684\"},{\"id\": 276, \"listId\": 1, \"name\": \"Item 276\"},{\"id\": 736, \"listId\": 3, \"name\": null},{\"id\": 926, \"listId\": 4, \"name\": null},{\"id\": 808, \"listId\": 4, \"name\": \"Item 808\"}]"
        val parsedData = viewModel.parseJsonData(fetchedData)

        val expectedParsedData = listOf(ParentData(1, listOf(MyData(276,1,"Item 276"), MyData(684, 1, "Item 684")), true),
                                        ParentData(4, listOf(MyData(808, 4, "Item 808")), true))
        Assert.assertEquals(expectedParsedData, parsedData)
    }
}