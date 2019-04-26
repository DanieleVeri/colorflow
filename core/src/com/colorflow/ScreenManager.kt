package com.colorflow

import java.util.ArrayList

enum class ScreenType {LOAD, MENU, PLAY, SHOP}

object ScreenManager {
    private var _cb_list: List<(ScreenType)->Unit> = ArrayList()

    fun set(screen: ScreenType) {
        _cb_list.map { it(screen) }
    }

    fun add_cb(cb: (ScreenType)->Unit) {
        _cb_list += cb
    }

    fun rem_cb(cb: (ScreenType)->Unit) {
        _cb_list -= cb
    }
}