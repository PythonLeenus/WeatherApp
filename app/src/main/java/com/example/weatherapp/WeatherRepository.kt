package com.example.weatherapp

import androidx.lifecycle.LiveData
import com.example.weatherapp.DataBase.AllWeatherEntity
import com.example.weatherapp.DataBase.CityLatLong
import com.example.weatherapp.DataBase.PlaceName
import com.example.weatherapp.DataBase.WeatherDao


class WeatherRepository(val inter: RetroApiInterface, private val dao: WeatherDao) {

    suspend fun getCurrentWeather(
        latitude: String,
        longitude: String,
        apiKey: String,
        unit: String
    ) = inter.getCurrentWeather(latitude, longitude, apiKey, unit)

    suspend fun getGeoloaction(googleApi: String) = inter.getGeoloaction(googleApi)

    suspend fun getCurrentCity(latLng: String, cagedDataKey: String) =
        inter.getCurrentCity(latLng, cagedDataKey)

    fun getAllWeather(): LiveData<AllWeatherEntity> {
        return dao.getAllWeather()
    }

    fun insertWeather(weather: AllWeatherEntity) {
        return dao.insertWeather(weather)
    }

    fun getLatLong(): LiveData<CityLatLong> {
        return dao.getLatLong()
    }

    fun insertLatLong(cityLatLong: CityLatLong) {
        return dao.insertLatLong(cityLatLong)
    }

    fun getPlaceName(): PlaceName {
        return dao.getPlaceName()
    }

    fun insertPlaceName(placeName: PlaceName) {
        return dao.insertPlaceName(placeName)
    }
}
