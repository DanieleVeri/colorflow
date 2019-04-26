package com.colorflow.persistence

data class DataRing (
        val id: String,
        val cost: Int,
        val purchased: Boolean,
        val used: Boolean,
        val src: String)