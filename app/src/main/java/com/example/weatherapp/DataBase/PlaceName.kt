package com.example.weatherapp.DataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placeName")
data class PlaceName(
    val place: String
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
