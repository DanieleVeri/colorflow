package com.colorflow.ads

class AdManager(protected val ad_handler: IAdHandler) {
    var available = true

    fun show() {
        if(!available) return
        ad_handler.show_ad()
    }

    fun is_rewarded(): Boolean {
        return ad_handler.is_rewarded()
    }
}