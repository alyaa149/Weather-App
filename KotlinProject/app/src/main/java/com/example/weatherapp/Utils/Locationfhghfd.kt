package com.example.weatherapp.Utils

//public fun isLocationEnabled(): Boolean {
//    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//}
//
//public fun enableLocationServices() {
//    Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
//    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//    startActivity(intent)
//}
//
//public fun checkPermissions(): Boolean {
//    return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) ||
//            (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED)
//}
//
//
//
//public fun getFreshLocation() {
//    if (ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        return
//    }
//    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//        location?.let {
//            locationState.value = it
//        } ?: run {
//            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
//
//
//
//
//fun getAddressFromLocation(lat: Double, lon: Double, context: Context): String {
//    var address = ""
//
//    val geocoder = Geocoder(context)
//    val addresses = geocoder.getFromLocation(lat, lon, 1)
//    if (!addresses.isNullOrEmpty()) {
//        address = addresses[0].getAddressLine(0) ?: "Address not found"
//    } else {
//        address= "Address not found"
//    }
//
//    return address
//}
//
//
//fun openLocationInMap(lat: Double, lon: Double, context: Context) {
//    val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
//    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//    context.startActivity(mapIntent)
//}
//fun openSmsWithAddress(address: String, phoneNumber: String, context: Context) {
//    val smsUri = Uri.parse("smsto:$phoneNumber")
//    val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
//    smsIntent.putExtra("sms_body", address)
//    context.startActivity(smsIntent)
//}