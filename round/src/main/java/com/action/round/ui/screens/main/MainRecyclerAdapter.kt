package com.action.round.ui.screens.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.action.round.R
import com.action.round.data.models.Training

class MainRecyclerAdapter(
    private val onLongClick: (Training) -> Unit,
    private val onClick: (Training) -> Unit,
) : RecyclerView.Adapter<MainRecyclerAdapter.TrainingViewHolder>() {

    private var currentList = listOf<Training>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        return TrainingViewHolder(
            itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_training, parent, false),
            onLongClick = onLongClick,
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

    fun submitList(newList: List<Training>) {
        currentList = newList
        onListUpdate()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onListUpdate() {
        notifyDataSetChanged()
    }

    inner class TrainingViewHolder(
        itemView: View,
        private val onLongClick: (Training) -> Unit,
        private val onClick: (Training) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int) {
            val titleOfTraining = itemView.findViewById<TextView>(R.id.tvWorkoutTitle)
            val roundsOfTraining = itemView.findViewById<TextView>(R.id.tvRounds)
            titleOfTraining.text = currentList[position].title
            roundsOfTraining.text = currentList[position].exercises.size.toString()

            itemView.setOnLongClickListener {
                onLongClick(currentList[position])
                true
            }

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
