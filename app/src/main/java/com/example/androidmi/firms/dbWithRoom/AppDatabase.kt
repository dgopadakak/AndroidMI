package com.example.androidmi.firms.dbWithRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidmi.firms.TravelCompanyOperator

@Database(entities = [ TravelCompanyOperator::class ], version=5, exportSchema = false)
abstract class AppDatabase: RoomDatabase()
{
    public abstract fun groupOperatorDao(): TravelCompanyOperatorDao
}