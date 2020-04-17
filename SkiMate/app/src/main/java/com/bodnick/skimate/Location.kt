package com.bodnick.skimate

import java.io.Serializable

data class Location (
    var address: String,
    var lat: String,
    var lng: String
) : Serializable {
    // Required by Firebase to cast to a custom object
    // Calls the primary constructor with default arguments
    constructor() : this("","","")
}