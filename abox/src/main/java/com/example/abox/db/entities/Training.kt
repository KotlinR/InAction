package com.example.abox.db.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Training(
    val id: String,
    var title: String,
    val exercises: MutableList<String>,
) : Parcelable