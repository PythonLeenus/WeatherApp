package com.example.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.DataBase.AllWeatherEntity
import com.example.weatherapp.DataBase.CityLatLong
import com.example.weatherapp.DataBase.PlaceName
import com.example.weatherapp.GeolocationApi.Geolocation
import com.example.weatherapp.OpenWeatherAPI.AllWeather
import com.example.weatherapp.ReverseGeocoding.CurrentCity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(val repo: WeatherRepository) : ViewModel() {
    var currentWeather = MutableLiveData<AllWeather>()
    var currentLocation = MutableLiveData<Geolocation>()
    var currentCity = MutableLiveData<CurrentCity>()

    val getAllWeather : LiveData<AllWeatherEntity>
    val getLatLong : LiveData<CityLatLong>

    init {
        getAllWeather = repo.getAllWeather()
        getLatLong = repo.getLatLong()
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }


    fun getCurrentWeather(latitude: String, longitude: String, apiKey: String, unit: String) {
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            var res = repo.getCurrentWeather(latitude, longitude, apiKey, unit)
            if(res.isSuccessful) {
                currentWeather.postValue(res.body())
            }

        }
    }


    fun getGeoloaction(googleApi: String) {
        CoroutineScope(Dispatchers.IO+ coroutineExceptionHandler).launch {
            var res = repo.getGeoloaction(googleApi)
            if(res.isSuccessful) {
                currentLocation.postValue(res.body())
            }

        }
    }

    fun getCurrentCity(latLng:String, cagedDataKey:String) {
        CoroutineScope(Dispatchers.IO+ coroutineExceptionHandler).launch {
            var res = repo.getCurrentCity(latLng, cagedDataKey)
            if(res.isSuccessful) {
                currentCity.postValue(res.body())
            }

        }
    }

    fun insertWeather(weather: AllWeatherEntity) = viewModelScope.launch{
        repo.insertWeather(weather)
    }


    fun insertLatLong(cityLatLong: CityLatLong) = viewModelScope.launch {
        repo.insertLatLong(cityLatLong)
    }


    fun getPlaceName() = repo.getPlaceName()



    fun insertPlaceName(placeName: PlaceName) = viewModelScope.launch {
        repo.insertPlaceName(placeName)
    }
}