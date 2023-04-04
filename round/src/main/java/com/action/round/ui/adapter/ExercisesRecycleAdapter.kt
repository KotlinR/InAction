package com.action.round.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.action.round.R
import com.action.round.ui.adapter.item.touch.ItemTouchHelperAdapter
import com.action.round.ui.adapter.item.touch.ItemTouchHelperViewHolder

class ExercisesRecycleAdapter(
    private var exercises: List<String>,
    private val onSwipe: (position: Int) -> Unit,
    private val onMove: (from: Int, to: Int) -> Unit,
//    private val onClick: (Int) -> Unit,
) : ListAdapter<String, ExercisesRecycleAdapter.RoundViewHolder>(ExercisesDiffUtilCallback()),
    ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundViewHolder {
        return RoundViewHolder(
            view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.round_and_exercises, parent, false),
//            onClick = onClick,
        )
    }

    override fun onBindViewHolder(holder: RoundViewHolder, position: Int) {
        holder.onBind(exercises[position], position)
    }

    override fun getItemCount() = exercises.size

    override fun onViewRecycled(holder: RoundViewHolder) {
        super.onViewRecycled(holder)
        holder.onUnbind()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        onMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        onSwipe(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onListUpdate() {
        submitList(exercises)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateExercises(updatedExercises: List<String>) {
        exercises = updatedExercises
        submitList(exercises)
    }

    class RoundViewHolder(
        private val view: View,
//        private val onClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(view), ItemTouchHelperViewHolder {

        override fun onItemSelected() {
            view.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            view.setBackgroundColor(Color.WHITE)
        }

        @SuppressLint("SetTextI18n")
        fun onBind(exercise: String, position: Int) {
            view.apply {
                findViewById<TextView>(R.id.tvRoundOfTraining).text = "Round [ ${position + 1} ]"
                findViewById<TextView>(R.id.etExerciseOfTraining).text = exercise

                setOnClickListener {
//                    onClick(position)
                }
            }
        }

        fun onUnbind() {
            view.setOnClickListener(null)
        }
    }

    class ExercisesDiffUtilCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
}