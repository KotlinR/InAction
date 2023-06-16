package com.action.round.ui.screens.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.action.round.R
import com.action.round.data.models.Training
import com.action.round.ui.recycler.ItemTouchHelperAdapter
import com.action.round.ui.recycler.ItemTouchHelperViewHolder

class MainRecyclerAdapter(
    private val onClick: (Training) -> Unit,
    private val onSwipe: (Training) -> Unit,
) : RecyclerView.Adapter<MainRecyclerAdapter.TrainingViewHolder>(), ItemTouchHelperAdapter {

    private var currentList = mutableListOf<Training>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        return TrainingViewHolder(
            itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_training, parent, false),
            onClick = onClick,
        )
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount() = currentList.size

    override fun onViewRecycled(holder: TrainingViewHolder) {
        super.onViewRecycled(holder)
        holder.onUnbind()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
    }

    override fun onItemDismiss(position: Int) {
        onSwipe(currentList[position])
        currentList.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onListUpdate() {
        notifyDataSetChanged()
    }

    fun submitList(newList: List<Training>) {
        currentList = newList.toMutableList()
        onListUpdate()
    }

    inner class TrainingViewHolder(
        itemView: View,
        private val onClick: (Training) -> Unit,
    ) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.DKGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun onBind(position: Int) {
            val titleOfTraining = itemView.findViewById<TextView>(R.id.tvWorkoutTitle)
            val roundsOfTraining = itemView.findViewById<TextView>(R.id.tvRounds)
            titleOfTraining.text = currentList[position].title
            roundsOfTraining.text = currentList[position].exercises.size.toString()

            itemView.setOnClickListener {
                onClick(currentList[position])
            }
        }

        fun onUnbind() {
            itemView.setOnLongClickListener(null)
            itemView.setOnClickListener(null)
        }
    }
}
