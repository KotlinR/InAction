package com.example.abox.cra

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abox.R
import com.example.abox.db.entities.Training

class TrainingsRecyclerAdapter(
    private var trainings: List<Training>,
    private val onLongClick: (Training) -> Unit,
) : RecyclerView.Adapter<TrainingsRecyclerAdapter.TrainingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        return TrainingViewHolder(
            itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.workout_title, parent, false),
            onLongClick = onLongClick,
        )
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount() = trainings.size

    override fun onViewRecycled(holder: TrainingViewHolder) {
        super.onViewRecycled(holder)
        holder.onUnbind()
    }

    fun updateTrainings(allTrainings: List<Training>) {
        trainings = allTrainings
        notifyDataSetChanged()
    }

    inner class TrainingViewHolder(
        itemView: View,
        private val onLongClick: (Training) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int) {
            val titleOfTraining = itemView.findViewById<TextView>(R.id.tvWorkoutTitle)
            val roundsOfTraining = itemView.findViewById<TextView>(R.id.tvRounds)
            titleOfTraining.text = trainings[position].title
            roundsOfTraining.text = trainings[position].exercises.size.toString()

            itemView.setOnLongClickListener {
                onLongClick(trainings[position])
                true
            }
        }

        fun onUnbind() {
            itemView.setOnLongClickListener(null)
        }
    }
}