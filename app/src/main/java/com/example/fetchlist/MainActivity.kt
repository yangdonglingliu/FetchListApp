package com.example.fetchlist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        val parentRecyclerView = findViewById<RecyclerView>(R.id.parent_recycler_view)
        parentRecyclerView.setHasFixedSize(true)
        parentRecyclerView.layoutManager = LinearLayoutManager(this)

        val parentAdapter = ParentRecyclerViewAdapter(emptyList())
        parentRecyclerView.adapter = parentAdapter

        viewModel.parsedJsonLiveData.observe(this, Observer { parsedJsonLiveData ->
            if (parsedJsonLiveData != null) {
//                parentRecyclerView.adapter = ParentRecyclerViewAdapter(parsedJsonData)
                parentAdapter.updateParentData(parsedJsonLiveData)
            }
        })

        val filterEnableCheckBox = findViewById<CheckBox>(R.id.checkbox_filter)
        filterEnableCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.toggleFilterEnabledState(isChecked)
        }

    }
}