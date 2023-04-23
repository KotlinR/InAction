package com.action.round.data

// todo: remove after migration to db primary key (optional)
object IdGenerator {
    var id: Int = 0
        get() = ++field
        private set
}
