package com.action.round.ui.screens.training

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.action.round.R
import com.action.round.data.models.Exercise
import com.action.round.ui.recycler.ItemTouchHelperAdapter
import com.action.round.ui.recycler.ItemTouchHelperViewHolder
import com.action.round.utills.findAndUpdate
import com.action.round.utills.swap

class TrainingRecycleAdapter(
    private val onSwipe: ((position: Int) -> Unit)?,
    private val onMove: ((from: Int, to: Int) -> Unit)?,
    private val onExerciseChange: ((id: Int, newText: String) -> Unit)?,
    private val onLongClick: ((round: Int) -> Unit)?,
) : RecyclerView.Adapter<TrainingRecycleAdapter.RoundViewHolder>(),
    ItemTouchHelperAdapter {

    private var currentList = mutableListOf<Exercise>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundViewHolder {
        return RoundViewHolder(
            itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_round, parent, false),
            onExerciseChange = { id, newDescription ->
                currentList.findAndUpdate(
                    predicate = { it.id == id },
                    modify = { it.copy(description = newDescription) },
                )
                Log.d("!!!", "$currentList")
                onExerciseChange?.invoke(id, newDescription)
            },
            onLongClick = { round ->
                onLongClick?.invoke(round)
            }
        )
    }

    override fun onBindViewHolder(holder: RoundViewHolder, position: Int) {
        holder.onBind(currentList[position], position)
    }

    override fun getItemCount(): Int = currentList.size

    override fun onViewRecycled(holder: RoundViewHolder) {
        super.onViewRecycled(holder)
        holder.onUnbind()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        onMove?.invoke(fromPosition, toPosition)
        currentList.swap(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        onSwipe?.invoke(position)
        currentList.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onListUpdate() {
        notifyDataSetChanged()
    }

    fun submitList(newList: List<Exercise>) {
        currentList = newList.toMutableList()
        onListUpdate()
    }

    inner class RoundViewHolder(
        itemView: View,
        private val onExerciseChange: ((id: Int, newText: String) -> Unit)?,
        private val onLongClick: ((round: Int) -> Unit)?,
    ) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.DKGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun onBind(exercise: Exercise, position: Int) {
            itemView.apply {
                onFocusChangeListener
                val text = "Round [ ${adapterPosition + 1} ]"
                findViewById<TextView>(R.id.tvRoundOfTraining).text = text
                if (this@TrainingRecycleAdapter.onLongClick != null) {
                    findViewById<EditText>(R.id.etExerciseOfTraining).apply {
                        setText(exercise.description)
                        isEnabled = false
                    }
                    setOnLongClickListener {
                        onLongClick?.invoke(position + 1)
                        true
                    }
                } else {
                    findViewById<EditText>(R.id.etExerciseOfTraining).apply {
                        setOnFocusChangeListener { view, b ->
                            if (view == this && !b) {
                                val newText = itemView.findViewById<EditText>(R.id.etExerciseOfTraining).text
                                onExerciseChange?.invoke(exercise.id, newText?.toString().orEmpty())
                                Log.d("!!!", "$newText")
                            }
                        }
                        setText(exercise.description)
                        setSelection(exercise.description.length)
                    }
                }
            }
        }

        fun onUnbind() {
            itemView.setOnClickListener(null)
            itemView.setOnLongClickListener(null)
        }
    }
}
