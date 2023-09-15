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

    val parsedJsonLiveData: MutableLiveData<List<ParentData>> = MutableLiveData()
    val expandableStatesLiveData: MutableLiveData<Pair<Int?, List<Boolean>>> = MutableLiveData()

    private var parsedJsonData: List<ParentData> = emptyList()
    private var filteredParsedJsonData: List<ParentData> = emptyList()
    private var filterEnabled: Boolean = false

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
            parsedJsonLiveData.postValue(emptyList())
            expandableStatesLiveData.postValue(Pair(null, emptyList<Boolean>().toMutableList()))
            return
        }
        parsedJsonData = parseJsonData((jsonDataString))
        parsedJsonLiveData.postValue(parsedJsonData)
        expandableStatesLiveData.postValue(Pair(null, MutableList(parsedJsonData.size) {true}))

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

    internal fun parseJsonData(jsonData: String?): List<ParentData> {

        // skip the work
        if (jsonData.isNullOrEmpty()) {
            return emptyList()
        }

        // parse Json data with moshi code gen
        val moshi = Moshi.Builder().build()

        val myDataListType = Types.newParameterizedType(List::class.java, MyData::class.java)
        val adapter = moshi.adapter<List<MyData>>(myDataListType)
        val myDataList = adapter.fromJson(jsonData)

//        // filter out the items with null names
//        val myFilteredDataList = myDataList?.filter{!it.name.isNullOrEmpty()}

        return if (myDataList.isNullOrEmpty()) {
            emptyList()
        } else {
            // build a map with key: listId and value: list of MyData with the listId
            val listMap: Map<Int, List<MyData>> = myDataList.groupBy { it.listId }

            // build a list of ParentData using the map
            val parentDataList: List<ParentData> = listMap.map { (listId, items) ->
//                ParentData(listId, items.sortedBy { it.name!!.substringAfter("Item ").toInt() }, true)

                // sort the items based on name; if null or empty, put in front
                val sortedItems = items.sortedWith(compareBy(
                    {!it.name.isNullOrEmpty()},
                    {if (!it.name.isNullOrEmpty()) it.name.substringAfter("Item ").toInt() else 0}
                ))
                ParentData(listId, sortedItems)
            }

            // sort the list of ParentData by listId
            parentDataList.sortedBy { it.listId }
        }
    }

     fun toggleExpandableState(position: Int) {
         val expandableStatesList = expandableStatesLiveData.value?.second
         if (expandableStatesList.isNullOrEmpty()) return
         val newList = expandableStatesList.mapIndexed() {index, state ->
             if (index == position) !state else state
         }
         expandableStatesLiveData.value = Pair(position, newList)

    }

    fun toggleFilterEnabledState(boxChecked: Boolean) {
        if (parsedJsonData.isEmpty()) {
            return
        }

        // from disabled to enabled
        if (boxChecked) {
            filterEnabled = true
            if (filteredParsedJsonData.isEmpty()) {
                filteredParsedJsonData = parsedJsonData.map { ParentData ->
                    val filteredSubList = ParentData.subList.filter { MyData ->
                        !MyData.name.isNullOrEmpty()
                    }
                    ParentData.copy(subList = filteredSubList)
                }
            }
            // if not empty, it means the filtering process was done already and we can directly set value
            parsedJsonLiveData.value = filteredParsedJsonData
        }

        // from enabled to disabled
        else {
            filterEnabled = false
            parsedJsonLiveData.value = parsedJsonData
        }

    }
}