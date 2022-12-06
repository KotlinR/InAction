package com.sport.abox.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sport.abox.R
import com.sport.abox.adapter.item.touch.ItemTouchHelperAdapter
import com.sport.abox.adapter.item.touch.ItemTouchHelperViewHolder

class RoundsRecycleAdapter(
    private val exercises: MutableList<String>,
    private val reversUpdateExercises: (List<String>) -> Unit,
    private val onSwipe: (Int) -> Unit,
    private val onClick: (Int) -> Unit,
) : RecyclerView.Adapter<RoundsRecycleAdapter.RoundViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundViewHolder {
        return RoundViewHolder(
            view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.round_and_exercises, parent, false),
            onClick = onClick,
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
        val prev: String = exercises.removeAt(fromPosition)
        exercises.add(toPosition, prev)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        onSwipe(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onListUpdate() {
        reversUpdateExercises(exercises)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRounds(allExercises: List<String>) {
        exercises.clear()
        exercises.addAll(allExercises)
        notifyDataSetChanged()
    }

    class RoundViewHolder(
        private val view: View,
        private val onClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(view), ItemTouchHelperViewHolder {

        override fun onItemSelected() {
            view.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            view.setBackgroundColor(0)
        }

        @SuppressLint("SetTextI18n")
        fun onBind(exercise: String, position: Int) {
            view.apply {
                findViewById<TextView>(R.id.tvRoundOfTraining).text = "Round [ ${position + 1} ]"
                findViewById<TextView>(R.id.tvExerciseOfTraining).text = exercise

                setOnClickListener {
                    onClick(position)
                }
            }
        }

        fun onUnbind() {
            view.setOnLongClickListener(null)
            view.setOnClickListener(null)
        }
    }
}