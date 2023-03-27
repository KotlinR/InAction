package com.action.round.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Training(
    val id: Int,
    val title: String,
    val exercises: List<String>,
) : Parcelable