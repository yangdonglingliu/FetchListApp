package com.example.fetchlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "ChildRecyclerViewAdapter"
class ChildRecyclerViewAdapter (private val childDataList: List<MyData>)
    : RecyclerView.Adapter<ChildRecyclerViewAdapter.ChildRecyclerViewHolder>() {

    inner class ChildRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val childItemName: TextView = itemView.findViewById(R.id.child_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildRecyclerViewHolder {
//        Log.i(TAG, "onCreateViewHolder")

        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.child_item, parent, false)
        return ChildRecyclerViewHolder(adapterLayout)
    }

    override fun getItemCount() = childDataList.size

    override fun onBindViewHolder(holder: ChildRecyclerViewHolder, position: Int) {
//        Log.i(TAG, "onBindViewHolder, position $position")

        val childItem = childDataList[position]
        holder.childItemName.text = childItem.name
    }
}