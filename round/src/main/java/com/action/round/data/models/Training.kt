package com.action.round.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Training(
    val id: Int = 0,
    val title: String,
    val exercises: List<Exercise>,
) : Parcelable

