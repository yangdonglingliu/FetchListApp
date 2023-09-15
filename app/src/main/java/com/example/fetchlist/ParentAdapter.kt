package com.example.fetchlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//private const val TAG = "ParentRecyclerViewAdapter"
class ParentRecyclerViewAdapter (private val viewModel: MyViewModel,
                                 private var parentDataList: List<ParentData>,
                                 private var expandableStatesList: List<Boolean>)
    : RecyclerView.Adapter<ParentRecyclerViewAdapter.ParentRecyclerViewHolder>() {

    inner class ParentRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val parentListCard: CardView = itemView.findViewById(R.id.parent_list_card)
        val parentListId: TextView = itemView.findViewById(R.id.parent_list_id)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentRecyclerViewHolder {
//        Log.i(TAG, "onCreateViewHolder")
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.parent_item, parent, false)

        return ParentRecyclerViewHolder(adapterLayout)
    }

    override fun getItemCount() = parentDataList.size

    override fun onBindViewHolder(holder: ParentRecyclerViewHolder, position: Int) {
//        Log.i(TAG, "onBindViewHolder, position $position")

        if (parentDataList.isNotEmpty()) {
            val parentData = parentDataList[position]

            holder.parentListId.text = parentData.listId.toString()

            holder.childRecyclerView.setHasFixedSize(true)
            holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.childRecyclerView.adapter = ChildRecyclerViewAdapter(parentData.subList)

            // expandable functionality
            if (expandableStatesList.isEmpty()) {
                holder.childRecyclerView.visibility = View.GONE
            }
            else {
                holder.childRecyclerView.visibility =
                    if (expandableStatesList[position]) View.GONE else View.VISIBLE

                holder.parentListCard.setOnClickListener {
                    viewModel.toggleExpandableState(position)
//                parentData.isExpandable = !parentData.isExpandable
//                notifyItemChanged(position)
                }
//                Log.i(TAG, "onClickListener set up.")
            }
        }
    }

    fun updateParentData(newData: List<ParentData>) {
        parentDataList = newData
//        Log.i(TAG, "updateParentData is called.")
        notifyDataSetChanged()
    }

    fun updateExpandableStates(position: Int?, newData: List<Boolean>) {
        expandableStatesList = newData
        if (position == null) {
            notifyDataSetChanged()
//            Log.i(TAG, "expandableStateList updated.")
        }
        else {
            notifyItemChanged(position)
//            Log.i(TAG, "notifyItemChanged called.")
//            Log.i(TAG, "current expandableStatesList is ${expandableStatesList.toString()}")
        }
    }


}