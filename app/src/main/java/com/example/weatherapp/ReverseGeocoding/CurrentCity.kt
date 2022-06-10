package com.example.weatherapp.ReverseGeocoding

import androidx.room.PrimaryKey


data class CurrentCity(
    val results: List<Result>
){
    @PrimaryKey(autoGenerate = false)
    var id:Int = 0
}