package com.example.androidmi.firms.dbWithRoom

import androidx.room.TypeConverter
import com.example.androidmi.firms.TravelCompany
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TravelCompanyOperatorConverter
{
    @TypeConverter
    fun fromGO(pharmacyChains: ArrayList<TravelCompany>): String
    {
        val gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder.create()
        return gson.toJson(pharmacyChains)
    }

    @TypeConverter
    fun toGO(data: String): ArrayList<TravelCompany>
    {
        val gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder.create()
        return try {
            val type: Type = object : TypeToken<ArrayList<TravelCompany>>() {}.type
            gson.fromJson(data, type)
        } catch (e: Exception) {
            ArrayList()
        }
    }
}