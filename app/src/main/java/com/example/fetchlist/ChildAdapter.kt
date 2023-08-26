package com.example.fetchlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChildRecyclerViewAdapter (private val childDataList: List<MyData>)
    : RecyclerView.Adapter<ChildRecyclerViewAdapter.ChildRecyclerViewHolder>() {

    inner class ChildRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val childItemName: TextView = itemView.findViewById(R.id.child_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildRecyclerViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.child_item, parent, false)
        return ChildRecyclerViewHolder(adapterLayout)
    }

    override fun getItemCount() = childDataList.size

    override fun onBindViewHolder(holder: ChildRecyclerViewHolder, position: Int) {
        val childItem = childDataList[position]
        holder.childItemName.text = childItem.name
    }
}