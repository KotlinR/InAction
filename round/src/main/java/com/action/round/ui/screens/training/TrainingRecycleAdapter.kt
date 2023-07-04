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
    private val onSwipe: ((position: Int) -> Unit)? = null,
    private val onMove: ((from: Int, to: Int) -> Unit)? = null,
    private val onExerciseChange: ((id: Int, newText: String) -> Unit)? = null,
    private val onLongClick: ((round: Int) -> Unit)? = null,
) : RecyclerView.Adapter<TrainingRecycleAdapter.RoundViewHolder>(),
    ItemTouchHelperAdapter {

    private var _currentList = mutableListOf<Exercise>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundViewHolder {
        return RoundViewHolder(
            itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_round, parent, false),
            onExerciseChange = { id, newDescription ->
                _currentList.findAndUpdate(
                    predicate = { it.id == id },
                    modify = { it.copy(description = newDescription) },
                )
                Log.d("!!!", "$_currentList")
                onExerciseChange?.invoke(id, newDescription)
            },
            onLongClick = onLongClick,
        )
    }

    override fun onBindViewHolder(holder: RoundViewHolder, position: Int) {
        holder.onBind(_currentList[position], position)
    }

    override fun getItemCount(): Int = _currentList.size

    override fun onViewRecycled(holder: RoundViewHolder) {
        super.onViewRecycled(holder)
        holder.onUnbind()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        onMove?.invoke(fromPosition, toPosition)
        _currentList.swap(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        onSwipe?.invoke(position)
        _currentList.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onListUpdate() {
        notifyDataSetChanged()
    }

    fun submitList(newList: List<Exercise>) {
        _currentList = newList.toMutableList()
        onListUpdate()
    }

    fun getList(): List<Exercise> = _currentList.toList()

    class RoundViewHolder(
        itemView: View,
        private val onExerciseChange: ((id: Int, newText: String) -> Unit)? = null,
        private val onLongClick: ((round: Int) -> Unit)? = null,
    ) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        private val tvRound: TextView? by lazy {
            itemView.findViewById(R.id.tvRoundOfTraining)
        }

        private val etRound: EditText? by lazy {
            itemView.findViewById(R.id.etExerciseOfTraining)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.DKGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun onBind(exercise: Exercise, position: Int) {
            itemView.apply {
                val text = "Round [ ${adapterPosition + 1} ]"
                tvRound?.text = text
                if (onLongClick != null) {
                    setBackgroundResource(R.drawable.bg_item_exercise)
                    etRound?.apply {
                        setText(exercise.description)
                        isEnabled = false
                    }
                    setOnLongClickListener {
                        onLongClick.invoke(position + 1)
                        true
                    }
                } else {
                    etRound?.apply {
                        setOnFocusChangeListener { view, focus ->
                            if (view == this && !focus) {
                                onExerciseChange?.invoke(exercise.id, this.text.toString())
                                Log.d("!!!", text)
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
            etRound?.onFocusChangeListener = null
        }
    }
}
