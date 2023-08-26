package com.example.fetchlist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        val refreshButton = findViewById<Button>(R.id.refreshButton)
        refreshButton.setOnClickListener {
            viewModel.refreshData()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        viewModel.fetchedJsonData.observe(this, Observer { fetchedJsonData ->
            if (fetchedJsonData != null) {
                recyclerView.adapter = ItemAdapter(fetchedJsonData)
                recyclerView.setHasFixedSize(true)
            }
        })
    }
}