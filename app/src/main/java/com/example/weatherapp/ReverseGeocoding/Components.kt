package com.example.weatherapp.ReverseGeocoding


import com.google.gson.annotations.SerializedName

data class Components(
    val city: String,
    val country: String,
    @SerializedName("country_code")
    val countryCode: String,
    val county: String,
    val state: String,
)