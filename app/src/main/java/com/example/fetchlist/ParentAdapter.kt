package com.example.fetchlist

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ParentRecyclerViewAdapter (private val parentDataList: List<ParentData>)
    : RecyclerView.Adapter<ParentRecyclerViewAdapter.ParentRecyclerViewHolder>() {

    inner class ParentRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val parentListCard: CardView = itemView.findViewById(R.id.parent_list_card)
        val parentListId: TextView = itemView.findViewById(R.id.parent_list_id)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentRecyclerViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.parent_item, parent, false)
        return ParentRecyclerViewHolder(adapterLayout)
    }

    override fun getItemCount() = parentDataList.size

    override fun onBindViewHolder(holder: ParentRecyclerViewHolder, position: Int) {
        val parentData = parentDataList[position]

        holder.parentListId.text = parentData.listId.toString()

        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.childRecyclerView.adapter = ChildRecyclerViewAdapter(parentData.subList)

        // expandability
        holder.childRecyclerView.visibility = if (parentData.isExpandable) View.GONE else View.VISIBLE

        holder.parentListCard.setOnClickListener {
            parentData.isExpandable = !parentData.isExpandable
            notifyItemChanged(position)
        }
    }


}