package com.example.weatherapp.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [AllWeatherEntity::class, CityLatLong::class, PlaceName::class], version = 3, exportSchema = false)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun weatherDao() : WeatherDao

    companion object {
        var INSTANCE: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase? {
            if(INSTANCE == null) {
                synchronized(WeatherDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        WeatherDatabase::class.java, "weather.db")
                        .allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }
        fun destroyInstance(){
            INSTANCE = null
        }
    }


}