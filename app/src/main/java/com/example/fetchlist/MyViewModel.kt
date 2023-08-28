package com.example.fetchlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class MyViewModel : ViewModel() {
    private val client = OkHttpClient()
    val parsedJsonData: MutableLiveData<MutableList<ParentData>?> = MutableLiveData()

    init {
        fetchData()
    }

    private fun fetchData() {
        val request = Request.Builder()
            .url("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw Exception("Fetch data call failed.")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonDataString: String? = response.body?.string()
                    if (jsonDataString != null) {
                        parsedJsonData.postValue(parseJsonData(jsonDataString))
                    } else{
                        throw Exception("The response body is null.")
                    }
                } else {
                    throw Exception("The response is not successful.")
                }
            }
        })
    }

//    fun refreshData() {
//        fetchData()
//    }

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