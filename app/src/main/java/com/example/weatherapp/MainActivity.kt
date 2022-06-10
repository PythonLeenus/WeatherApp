package com.example.weatherapp


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.DataBase.AllWeatherEntity
import com.example.weatherapp.DataBase.CityLatLong
import com.example.weatherapp.DataBase.PlaceName
import com.example.weatherapp.DataBase.WeatherDatabase
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var vm: WeatherViewModel
    lateinit var placesClient: PlacesClient
    var latitude = ""
    var longitude = ""
    private val googleApi = "AIzaSyAiANxOSE30Kd-izZbZ4PnYIGo6ROppsMs" // Google Cloud API
    private val weatherApiKey = "863e72223d279e955d713a9437a9e6ce"    // Open Weather API
    private val openCageDataKey = "8eb888cd6f6142ee9203998161b2eb7c"  // OpenCage Geocoding API
    var units = "metric"  // imperial


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intr = RetroApiInterface.create()
        val dao = WeatherDatabase.getInstance(this)?.weatherDao()!!
        val repo = WeatherRepository(intr, dao)
        vm = WeatherViewModel(repo)

        refreshApp() // Swipe down to refresh page. More here:https://www.youtube.com/watch?v=oOIoRR0AiGo


//<----Codes for Google autocomplete Fragment. More here: https://developers.google.cn/maps/documentation/places/android-sdk/autocomplete--->
        Places.initialize(applicationContext, googleApi)
        placesClient = Places.createClient(this)
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                latitude = place.latLng?.latitude.toString()
                longitude = place.latLng?.longitude.toString()
                var placeName = place.address as String

                vm.insertPlaceName(PlaceName(placeName))
                vm.insertLatLong(CityLatLong(latitude, longitude))
            }

            override fun onError(status: Status) {
                println("An error occurred: $status")
            }
        })
//        <---End of Codes for Google autocomplete Fragment --->


        vm.getLatLong.observe(this) {
            try {
                vm.getCurrentWeather(it.Lat, it.Long, weatherApiKey, units)
            } catch (e: NullPointerException) {
                vm.getGeoloaction(googleApi)
            }
        }


        vm.currentLocation.observe(this) { it ->
            latitude = it.location.lat.toString()
            longitude = it.location.lng.toString()
            vm.insertLatLong(CityLatLong(latitude, longitude))
            vm.getCurrentCity("$latitude+$longitude", openCageDataKey)
        }


        vm.currentWeather.observe(this) {
//            val gson = GsonBuilder().setPrettyPrinting().create()
//            val pJson = gson.toJson(it)
//            println(pJson)
            var date = SimpleDateFormat("dd MMMM yyyy hh:mm a").format(Date())
            var maxTemp = "Max " + it.daily[0].temp.max.toString() + "º"
            var minTemp = "Min " + it.daily[0].temp.min.toString() + "º"
            var currentTemp = it.current.temp.toString() + "º"
            var feelsLike = "Feels like " + it.current.feelsLike.toString() + "º"
            var weatherType = it.current.weather[0].description.capitalize()

            val sunriseTime = Date(it.current.sunrise.toLong() * 1000)
            val sunriseFormatted = SimpleDateFormat("h:mm a").format(sunriseTime)
            var sunrise = "Sunrise: ${sunriseFormatted}"

            val sunsetTime = Date(it.current.sunset.toLong() * 1000)
            val sunsetFormatted = SimpleDateFormat("h:mm a").format(sunsetTime)
            var sunset = "Sunset: ${sunsetFormatted}"

            var humidity = "Humidity: ${it.current.humidity} %"
            var speedUnit = "metre/sec"
            if (units=="imperial") speedUnit = "miles/hour"
            var windSpeed = "Windspeed: ${it.current.windSpeed} $speedUnit"

            val weatherDetails = AllWeatherEntity(
                currentTemp, maxTemp, minTemp, date, feelsLike, weatherType, sunrise, sunset, humidity, windSpeed
            )
            vm.insertWeather(weatherDetails)
        }

        vm.currentCity.observe(this) {
            var city = it.results[0].components.city
            if (city == null) city = it.results[0].components.county
            var state = it.results[0].components.state
            var country = it.results[0].components.country
            var placeName = "$city, $state, $country"
            tv_city_name.text = placeName
            vm.insertPlaceName(PlaceName(placeName))
        }

        vm.getAllWeather.observe(this) {
            try {
                tv_date_and_time.text = it.date
                tv_day_max_temp.text = it.maxTemp
                tv_day_min_temp.text = it.minTemp
                tv_current_temp.text = it.currentTemp
                tv_feels_like.text = it.feelsLike
                tv_weather_type.text = it.weatherType
                tv_sunrise.text = it.sunrise
                tv_sunset.text = it.sunset
                tv_windSpeed.text = it.windSpeed
                tv_humidity.text = it.humidity
                tv_city_name.text = vm.getPlaceName().place

            } catch (e: NullPointerException) {
            }

        }

    }


    private fun refreshApp() {
        swipeToRefresh.setOnRefreshListener {
            try {
                vm.getCurrentWeather(
                    vm.getLatLong.value!!.Lat,
                    vm.getLatLong.value!!.Long,
                    weatherApiKey,
                    units
                )
            } catch (e: NullPointerException) {
                vm.getGeoloaction(googleApi)
            }
            Toast.makeText(this, "Page refreshed", Toast.LENGTH_SHORT).show()
            swipeToRefresh.isRefreshing = false
        }
    }
}