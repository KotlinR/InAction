package com.action.round.ui.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class SimpleItemTouchHelperCallback(
    private val adapter: ItemTouchHelperAdapter,
    private val movePermit: Boolean,
    private val context: Context,
    private val hideKeyboard: ((View) -> Unit)?,
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean = movePermit
    override fun isItemViewSwipeEnabled(): Boolean = true
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.6f
    override fun getSwipeVelocityThreshold(defaultValue: Float): Float = 0.0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START,
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(
            viewHolder.adapterPosition,
            target.adapterPosition,
        )
        return true
    }

    @SuppressLint("SwitchIntDef")
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val width = viewHolder.itemView.width
            val alpha = 1.0f + abs(dX) / width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
            if (alpha > 1.0f) {

                val icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)

                val itemHalfHeight = (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2
                val iconTop = viewHolder.itemView.top + itemHalfHeight - 88
                val iconBottom = viewHolder.itemView.bottom - itemHalfHeight + 88
                val iconLeft = viewHolder.itemView.right - (iconBottom - iconTop) - 14
                val iconRight = viewHolder.itemView.right - 14

                icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon?.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
                icon?.draw(c)
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            (viewHolder as ItemTouchHelperViewHolder).onItemSelected()
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG || actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            viewHolder?.let { hideKeyboard?.invoke(it.itemView) }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        (viewHolder as ItemTouchHelperViewHolder).onItemClear()
        recyclerView.post(adapter::onListUpdate)
    }
}
