package com.sport.abox.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abox.R

class RoundsRecycleAdapter(
    private val exercises: MutableList<String>,
    private val onLongClick: (Int) -> Unit,
    private val onClick: (Int) -> Unit,
) : RecyclerView.Adapter<RoundsRecycleAdapter.RoundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundViewHolder {
        return RoundViewHolder(
            view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.round_and_exercises, parent, false),
            onLongClick = onLongClick,
            onClick = onClick,
        )
    }

    override fun onBindViewHolder(holder: RoundViewHolder, position: Int) {
        holder.onBind(exercises[position],position)
    }

    override fun getItemCount() = exercises.size

    override fun onViewRecycled(holder: RoundViewHolder) {
        super.onViewRecycled(holder)
        holder.onUnbind()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRounds(allExercises: List<String>) {
        exercises.clear()
        exercises.addAll(allExercises)
        notifyDataSetChanged()
    }

    class RoundViewHolder(
        private val view: View,
        private val onLongClick: (Int) -> Unit,
        private val onClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun onBind(exercise: String, position: Int) {
            val roundOfTraining = view.findViewById<TextView>(R.id.tvRoundOfTraining)
            val exerciseOfRound = view.findViewById<TextView>(R.id.tvExerciseOfTraining)
            roundOfTraining.text = "Round [ ${position.plus(1)} ]"
            exerciseOfRound.text = exercise

            view.setOnLongClickListener {
                onLongClick(position)
                true
            }

            view.setOnClickListener {
                onClick(position)
            }
        }

        fun onUnbind() {
            view.setOnLongClickListener(null)
            view.setOnClickListener(null)
        }
    }
}