package com.example.androidmi.firms

data class TravelCompany(
    val name: String,
    var listOfTours: ArrayList<Tour> = ArrayList()
)
