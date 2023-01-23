package com.example.androidmi.firms.dbWithRoom

import androidx.room.*
import com.example.androidmi.firms.TravelCompanyOperator

@Dao
interface TravelCompanyOperatorDao
{
    @Query("SELECT * FROM TravelCompanyOperator")
    fun getAll(): List<TravelCompanyOperator?>?

    @Query("SELECT * FROM TravelCompanyOperator WHERE id = :id")
    fun getById(id: Int): TravelCompanyOperator

    @Insert
    fun insert(go: TravelCompanyOperator?)

    @Delete
    fun delete(go: TravelCompanyOperator?)
}