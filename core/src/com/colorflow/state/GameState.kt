package com.colorflow.state

class GameState(protected val persistence: IStorage,
                protected val set_screen_cb: (ScreenType)->Unit) {
    var coins: Int = 0
    var record: Int = 0
    var bomb_chance: Float = 0.0f
    var gold_chance: Float = 0.0f
    var track_list: List<Track> = ArrayList()
    var ring_list: List<Ring> = ArrayList()
    protected var purchased_tracks: ArrayList<String> = ArrayList()
    protected var purchased_rings: ArrayList<String> = ArrayList()

    var current_game: CurrentGame? = null

    fun load() {
        record = persistence.get_record()
        coins = persistence.get_coins()
        bomb_chance = persistence.get_bomb_chance()
        gold_chance = persistence.get_gold_chance()
        ring_list = persistence.get_rings()
        track_list = persistence.get_tracks()
    }

    fun persist() {
        persistence.transaction {
            persistence.set_coins(coins)
            persistence.set_record(record)
            persistence.set_bomb_chance(bomb_chance)
            persistence.set_gold_chance(gold_chance)
            purchased_rings.forEach { persistence.set_ring_purchased(it) }
            purchased_rings.clear()
            persistence.set_ring_selected(ring_list.find { it.used }!!.id)
            purchased_tracks.forEach { persistence.set_track_purchased(it) }
            purchased_tracks.clear()
        }
    }

    fun purchase_track(track: Track) {
        purchased_tracks.add(track.id)
        track_list.find { it.id == track.id }!!.purchased = true
        coins -= track.cost
    }

    fun purchase_ring(ring: Ring) {
        purchased_rings.add(ring.id)
        ring_list.find { it.id == ring.id }!!.purchased = true
        coins -= ring.cost
    }

    fun select_ring(ring_id: String) {
        ring_list.forEach {
            it.used = it.id == ring_id
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


