package com.action.round.utills

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity

fun ComponentActivity.toast(
    duration: Int = Toast.LENGTH_SHORT,
    message: () -> String,
) {
    Toast.makeText(
        this,
        message(),
        duration,
    ).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
    clearFocus()
}
