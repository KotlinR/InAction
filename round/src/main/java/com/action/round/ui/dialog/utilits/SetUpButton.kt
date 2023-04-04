package com.action.round.ui.dialog.utilits

import android.os.Bundle
import android.view.View
import android.widget.Button

fun View.setUpButton(id: Int, key: String, arguments: Bundle, onClick: (View) -> Unit) {
    findViewById<Button>(id)?.apply {
        arguments.getString(key)?.let {
            text = it
            setOnClickListener(onClick)
        } ?: run { visibility = View.GONE }
    }
}
