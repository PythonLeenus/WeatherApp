package com.example.weatherapp.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cityLatLong")
data class CityLatLong(
    val Lat: String,
    val Long: String
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}

