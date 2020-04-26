package com.bodnick.skimate

import java.io.Serializable

data class Course (
    var name: String?,
    var location: String,
    var lat: String,
    var lng: String,
    var weatherIcon: String?,
    var temp: String?,
    var precipitation: String?,
    var wind: String?,
    var windDeg: String?,
    var bearing: String,
    var id: String
) : Serializable {
    // Required by Firebase to cast to a custom object
    // Calls the primary constructor with default arguments
    constructor() : this("","","","", "", "", "", "", "", "", "")
}

