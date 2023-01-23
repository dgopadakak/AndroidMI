package com.example.androidmi.firms

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.androidmi.firms.dbWithRoom.TravelCompanyOperatorConverter
import java.util.*
import kotlin.collections.ArrayList

@Entity
class TravelCompanyOperator()
{
    @PrimaryKey
    private var id: Int = 1

    @TypeConverters(TravelCompanyOperatorConverter::class)
    private var travelCompanies: ArrayList<TravelCompany> = ArrayList()

    fun getTravelCompanies(): ArrayList<TravelCompany>
    {
        return travelCompanies
    }

    fun setTravelCompanies(newTravelCompanies: ArrayList<TravelCompany>)
    {
        travelCompanies = newTravelCompanies
    }

    fun setId(id: Int)
    {
        this.id = id
    }

    fun getId(): Int
    {
        return id
    }

    fun getToursNames(indexGroup: Int): ArrayList<String>
    {
        val arrayListForReturn: ArrayList<String> = ArrayList()
        for (i in travelCompanies[indexGroup].listOfTours)
        {
            arrayListForReturn.add(i.name)
        }
        return arrayListForReturn
    }

    fun getToursCountries(indexGroup: Int): ArrayList<String>
    {
        val arrayListForReturn: ArrayList<String> = ArrayList()
        for (i in travelCompanies[indexGroup].listOfTours)
        {
            arrayListForReturn.add(i.country)
        }
        return arrayListForReturn
    }

    fun getToursNumbers(indexGroup: Int): ArrayList<Int>
    {
        val arrayListForReturn: ArrayList<Int> = ArrayList()
        for (i in travelCompanies[indexGroup].listOfTours)
        {
            arrayListForReturn.add(i.num)
        }
        return arrayListForReturn
    }

    fun getAllToursYears(indexGroup: Int): ArrayList<String>
    {
        val arrayListForReturn: ArrayList<String> = ArrayList()
        for (i in travelCompanies[indexGroup].listOfTours)
        {
            arrayListForReturn.add(i.dateOfSale.substring(6))
        }
        return arrayListForReturn
    }

    fun getToursYears(indexGroup: Int): ArrayList<String>
    {
        val arrayListForReturn: ArrayList<String> = ArrayList()
        for (i in travelCompanies[indexGroup].listOfTours)
        {
            if (!arrayListForReturn.contains(i.dateOfSale.substring(6)))
            {
                arrayListForReturn.add(i.dateOfSale.substring(6))
            }
        }
        return arrayListForReturn
    }

    fun getTour(indexGroup: Int, indexExam: Int): Tour
    {
        return travelCompanies[indexGroup].listOfTours[indexExam]
    }

    fun sortTours(tcIndex: Int, sortIndex: Int)
    {
        if (sortIndex == 0)
        {
            val tempArrayListOfExamsNames: ArrayList<String> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                tempArrayListOfExamsNames.add(i.name.lowercase(Locale.ROOT))
            }
            tempArrayListOfExamsNames.sort()
            for (i in tempArrayListOfExamsNames)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    if (i == j.name.lowercase(Locale.ROOT)
                        && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }

        if (sortIndex == 1)
        {
            val tempArrayListOfTeacherNames: ArrayList<String> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                tempArrayListOfTeacherNames.add(i.country.lowercase(Locale.ROOT))
            }
            tempArrayListOfTeacherNames.sort()
            for (i in tempArrayListOfTeacherNames)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    if (i == j.country.lowercase(Locale.ROOT)
                        && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }

        if (sortIndex == 2)
        {
            val tempArrayListOfAuditory: ArrayList<Int> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                tempArrayListOfAuditory.add(i.num)
            }
            tempArrayListOfAuditory.sort()
            for (i in tempArrayListOfAuditory)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    if (i == j.num && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }

        if (sortIndex == 3)
        {
            val tempArrayListOfTeacherNames: ArrayList<GregorianCalendar> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                val d: List<String> = i.dateOfSale.split(".")
                tempArrayListOfTeacherNames.add(GregorianCalendar(d[2].toInt(), d[1].toInt(),
                    d[0].toInt()))
            }
            tempArrayListOfTeacherNames.sort()
            for (i in tempArrayListOfTeacherNames)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    val d: List<String> = j.dateOfSale.split(".")
                    val tempGregorianCalendar = GregorianCalendar(d[2].toInt(), d[1].toInt(),
                        d[0].toInt())
                    if (i == tempGregorianCalendar && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }

        if (sortIndex == 4)
        {
            val tempArrayListOfTeacherNames: ArrayList<GregorianCalendar> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                val d: List<String> = i.dateStart.split(".")
                tempArrayListOfTeacherNames.add(GregorianCalendar(d[2].toInt(), d[1].toInt(),
                    d[0].toInt()))
            }
            tempArrayListOfTeacherNames.sort()
            for (i in tempArrayListOfTeacherNames)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    val d: List<String> = j.dateStart.split(".")
                    val tempGregorianCalendar = GregorianCalendar(d[2].toInt(), d[1].toInt(),
                        d[0].toInt())
                    if (i == tempGregorianCalendar && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }

        if (sortIndex == 5)
        {
            val tempArrayListOfPeoples: ArrayList<Int> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                tempArrayListOfPeoples.add(i.sum)
            }
            tempArrayListOfPeoples.sort()
            for (i in tempArrayListOfPeoples)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    if (i == j.sum && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }

        if (sortIndex == 6)
        {
            val tempArrayListOfAbstract: ArrayList<Int> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                tempArrayListOfAbstract.add(i.isSoldOut)
            }
            tempArrayListOfAbstract.sort()
            for (i in tempArrayListOfAbstract)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    if (i == j.isSoldOut && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }

        if (sortIndex == 7)
        {
            val tempArrayListOfComment: ArrayList<String> = ArrayList()
            val tempArrayListOfPharmacies: ArrayList<Tour> = ArrayList()
            for (i in travelCompanies[tcIndex].listOfTours)
            {
                tempArrayListOfComment.add(i.comment.lowercase(Locale.ROOT))
            }
            tempArrayListOfComment.sort()
            for (i in tempArrayListOfComment)
            {
                for (j in travelCompanies[tcIndex].listOfTours)
                {
                    if (i == j.comment.lowercase(Locale.ROOT)
                        && !tempArrayListOfPharmacies.contains(j))
                    {
                        tempArrayListOfPharmacies.add(j)
                        break
                    }
                }
            }
            travelCompanies[tcIndex].listOfTours = tempArrayListOfPharmacies
        }
    }
}