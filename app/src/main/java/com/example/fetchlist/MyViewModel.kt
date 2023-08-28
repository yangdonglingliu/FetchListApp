package com.example.fetchlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.Exception

class MyViewModel : ViewModel() {

    val parsedJsonData: MutableLiveData<MutableList<ParentData>?> = MutableLiveData()

    init {
        viewModelScope.launch {
            fetchAndParseData()
        }
    }

    private suspend fun fetchAndParseData() {
            try {
                val jsonDataString: String = fetchData()
                parsedJsonData.postValue(parseJsonData(jsonDataString))
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    private suspend fun fetchData() : String {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body!!.string()
            } else {
                throw IOException("The network response is not successful.")
            }
        }
    }

    private fun parseJsonData(jsonData: String): MutableList<ParentData>? {

        // parse Json data with moshi code gen
        val moshi = Moshi.Builder().build()

        val myDataListType = Types.newParameterizedType(List::class.java, MyData::class.java)
        val adapter = moshi.adapter<List<MyData>>(myDataListType)
        val myDataList = adapter.fromJson(jsonData)

        // filter out the items with null or empty names
        val myFilteredDataList = myDataList?.filter{!it.name.isNullOrEmpty()}

        return if (myFilteredDataList.isNullOrEmpty()) {
            null
        } else {
            // build a map with key: listId and value: list of MyData with the listId
            val listMap: Map<Int, List<MyData>> = myFilteredDataList.groupBy { it.listId }

            // build a list of ParentData using the map
            val parentDataList: List<ParentData> = listMap.map { (listId, items) ->
                ParentData(listId, items.sortedBy { it.name!!.substringAfter("Item ").toInt() }, true)
            }

            // sort the list of ParentData by listId
            parentDataList.sortedBy { it.listId }.toMutableList()
        }
    }
}