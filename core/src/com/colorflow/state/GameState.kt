package com.colorflow.state

class GameState(protected val persistence: IStorage,
                protected val set_screen_cb: (ScreenType)->Unit) {
    var coins: Int = 0
    var record: Int = 0
    var track_list: List<Track> = ArrayList()
    var ring_list: List<Ring> = ArrayList()
    var purchased_tracks: ArrayList<String> = ArrayList()
    var purchased_rings: ArrayList<String> = ArrayList()

    var current_game: CurrentGame? = null

    fun load() {
        record = persistence.get_record()
        coins = persistence.get_coins()
        ring_list = persistence.get_rings()
        track_list = persistence.get_tracks()
    }

    fun persist() {
        persistence.transaction {
            persistence.set_coins(coins)
            persistence.set_record(record)
            purchased_rings.forEach { persistence.set_ring_purchased(it) }
            purchased_rings.clear()
            persistence.set_ring_selected(ring_list.find { it.used }!!.id)
            purchased_tracks.forEach { persistence.set_track_purchased(it) }
            purchased_tracks.clear()
        }
    }

    fun set_screen(screen: ScreenType) {
        set_screen_cb(screen)
    }
}

enum class ScreenType {
    LOAD,
    MENU,
    PLAY,
    SHOP,
    TRACK_SELECTION,
    GAME_OVER}

data class CurrentGame (
    val selected_track: String,
    var score: Score = Score(),
    var gameover: Boolean = false,
    var paused: Boolean = false,
    var started: Boolean = false)

data class Score (
    var multiplier: Float = 1f,
    var points: Int = 0,
    var coins: Int = 0)

data class Track (
    val id: String,
    val cost: Int,
    var purchased: Boolean,
    val src: String)

data class Ring (
    val id: String,
    val cost: Int,
    var purchased: Boolean,
    var used: Boolean,
    val src: String)


