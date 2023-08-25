package com.example.fetchlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MyViewModel : ViewModel() {
    private val client = OkHttpClient()
    val fetchedJsonData: MutableLiveData<JSONObject> = MutableLiveData()

    init {
        fetchData()
    }

    private fun fetchData() {
        val request = Request.Builder()
            .url("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonDataString = response.body?.string()
                    if (jsonDataString != null) {
                        val jsonObject = JSONObject(jsonDataString)
                        fetchedJsonData.postValue(jsonObject)
                    } else {
                        TODO()
                    }
                } else {
                    TODO("Not yet implemented")
                }
            }
        })
    }

    fun refreshData() {
        fetchData()
    }

    private fun parseJsonData(jsonData: JSONObject): List<MyData> {
        val dataList = mutableListOf<MyData>()

        return dataList
    }
}