package com.action.round.data

// todo: remove
object IdGenerator {
    var id: Int = 0
        get() = ++field
}
