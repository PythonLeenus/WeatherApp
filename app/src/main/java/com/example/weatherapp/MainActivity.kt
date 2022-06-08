package com.example.weatherapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var vm: WeatherViewModel
    lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val googleApi = "AIzaSyAiANxOSE30Kd-izZbZ4PnYIGo6ROppsMs" // Google Cloud API
        val weatherApiKey = "863e72223d279e955d713a9437a9e6ce"    // Open Weather API
        val openCageDataKey = "8eb888cd6f6142ee9203998161b2eb7c"  // OpenCage Geocoding API

        val intr = RetroApiInterface.create()
        val repo = WeatherRepository(intr)
        vm = WeatherViewModel(repo)

        var units = "metric"  //imperial

        vm.getGeoloaction(googleApi)

        vm.currentLocation.observe(this){ it ->
//            println(it)
            var latitude = it.location.lat.toString()
            var longitude = it.location.lng.toString()

            vm.getCurrentCity("$latitude+$longitude", openCageDataKey)
            vm.getCurrentWeather(latitude, longitude, weatherApiKey, units)
        }

//<----Codes for Google autocomplete Fragment. More here: https://developers.google.cn/maps/documentation/places/android-sdk/autocomplete--->
        Places.initialize(getApplicationContext(), googleApi)
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
                println("Place: ${place.address}, ${place.latLng}")
                tv_city_name.text = place.address
                var latitude = place.latLng?.latitude.toString()
                var longitude = place.latLng?.longitude.toString()
                vm.getCurrentWeather(latitude, longitude, weatherApiKey, units)
            }

            override fun onError(status: Status) {
                println("An error occurred: $status")
            }
        })
//        <---End of Codes for Google autocomplete Fragment --->



        vm.currentWeather.observe(this) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val pJson = gson.toJson(it)
//            println(pJson)
            tv_date_and_time.text = SimpleDateFormat("dd/M/yyyy hh:mm a").format(Date())
            tv_day_max_temp.text = "Max " + it.daily[0].temp.max.toString() + "º"
            tv_day_min_temp.text = "Min " + it.daily[0].temp.min.toString() + "º"
            tv_current_temp.text = it.current.temp.toString() + "º"
            tv_feels_like.text = "Feels like " + it.current.feelsLike.toString() + "º"
            tv_weather_type.text = it.current.weather[0].description.capitalize()
        }

        vm.currentCity.observe(this){
            var city = it.results[0].components.city
            if(city == null) city = it.results[0].components.county
            var state = it.results[0].components.state
            var country = it.results[0].components.country
            var placeName = "$city, $state, $country"
            tv_city_name.text = placeName
        }

    }
}