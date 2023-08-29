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
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MyViewModel : ViewModel() {

    val parsedJsonData: MutableLiveData<List<ParentData>?> = MutableLiveData()
    init {
        viewModelScope.launch {
            fetchAndParseData("https://fetch-hiring.s3.amazonaws.com/hiring.json".toHttpUrl())
        }
    }

    internal suspend fun fetchAndParseData(address: HttpUrl) {
        lateinit var jsonDataString: String
        try {
            jsonDataString= fetchData(address)

        } catch (e: IOException) { // IO Exception during fetching
            e.printStackTrace()
            parsedJsonData.postValue(null)
            return
        }

        parsedJsonData.postValue(parseJsonData(jsonDataString))

    }

    internal suspend fun fetchData(address: HttpUrl) : String {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(address)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {

                // successful response should have a non-null body (can be empty), but just in case..
                val fetchedData = response.body?.string()
                if (fetchedData.isNullOrEmpty()) {
                    ""
                } else {
                    fetchedData
                }
            } else {
                throw IOException("The network response is not successful.")
            }
        }
    }

    internal fun parseJsonData(jsonData: String?): List<ParentData>? {

        // skip the work
        if (jsonData.isNullOrEmpty()) {
            return null
        }

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
            parentDataList.sortedBy { it.listId }
        }
    }

//     fun toggleExpandableState(parentDataList: List<ParentData>, position: Int) {
//
//        val updatedParentDataList = parentDataList.toMutableList()
//        val parentData = updatedParentDataList[position]
//        parentData.isExpandable = !parentData.isExpandable
//        parsedJsonData.value = updatedParentDataList

//    }
}