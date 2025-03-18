package com.example.mydevotional.model

data class BibleResponse(
    val reference: String,
    val verses: List<Verses>,
    val text: String
)