package com.example.smartfishbowl.api

import java.time.LocalTime

data class
Token(var accessToken: String, var firebaseToken: String)
data class CurrentDevice(var deviceId: Long)
data class Setting(var temperature: Double, var waterLevel: Int?, var ph: Double, var turbidity: Double, var deviceId: Long)
data class FoodSetting(var deviceId: Long,
                       var firstTime: String,
                       var secondTime: String,
                       var thirdTime: String,
                       var numberOfFirstFeedings: Int?,
                       var numberOfSecondFeedings: Int?,
                       var numberOfThirdFeedings: Int?)
data class Getting(var measuredTemperature: Double, var measuredWaterLevel: Int?, var measuredPh: Double, var measuredTurbidity: Double)