package com.bodnick.skimate

data class Course (
    var name: String?,
    var location: String?,
    var lat: String,
    var lng: String,
//    val thumbnail: String,
    var weatherIcon: String?,
    var temp: String?,
    var precipitation: String?,
    var wind: String?
)

