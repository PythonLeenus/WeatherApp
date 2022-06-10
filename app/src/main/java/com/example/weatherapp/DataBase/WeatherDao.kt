package com.example.weatherapp.DataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * from allWeather where id =0")
    fun getAllWeather(): LiveData<AllWeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: AllWeatherEntity)

    @Query("SELECT * from cityLatLong where id =0")
    fun getLatLong(): LiveData<CityLatLong>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLatLong(cityLatLong: CityLatLong)

    @Query("SELECT * from placeName where id =0")
    fun getPlaceName(): PlaceName

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaceName(placeName: PlaceName)


}