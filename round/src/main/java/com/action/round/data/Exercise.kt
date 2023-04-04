package com.action.round.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    val id: Int = IdGenerator.id, // todo: replace with id from db
    val description: String = "",
) : Parcelable
