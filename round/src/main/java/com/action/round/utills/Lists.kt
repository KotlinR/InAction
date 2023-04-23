package com.action.round.utills

fun <T> MutableList<T>.findAndUpdate(
    predicate: (T) -> Boolean,
    modify: (T) -> T,
) {
    firstOrNull(predicate)?.let { toModify ->
        val modified = modify(toModify)
        val index = indexOf(toModify)
        removeAt(index)
        add(index, modified)
    }
}

fun <T> MutableList<T>.swap(from: Int, to: Int) {
    val removed: T = removeAt(from)
    add(to, removed)
}
