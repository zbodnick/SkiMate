package com.bodnick.skimate
import java.io.Serializable

data class Forecast (
    var day: String,
    var iconID: String,
    var temp: String
) : Serializable {
    constructor() : this("","","")
}