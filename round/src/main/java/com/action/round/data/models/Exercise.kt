package com.action.round.data.models

import android.os.Parcelable
import com.action.round.data.IdGenerator
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    val id: Int = IdGenerator.id,
    val description: String = "",
) : Parcelable
