package com.example.androidmi

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class TourDetailsDialogFragment: android.app.DialogFragment()
{
    private val exceptionTag = "PharmacyDetailsDialogFragment"

    interface OnInputListenerSortId
    {
        fun sendInputSortId(sortId: Int)
    }

    lateinit var onInputListenerSortId: OnInputListenerSortId

    private lateinit var textViewNameTitle: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewCountryTitle: TextView
    private lateinit var textViewCountry: TextView
    private lateinit var textViewNumTitle: TextView
    private lateinit var textViewNum: TextView
    private lateinit var textViewDateOfSaleTitle: TextView
    private lateinit var textViewDateOfSale: TextView
    private lateinit var textViewDateStartTitle: TextView
    private lateinit var textViewDateStart: TextView
    private lateinit var textViewSumTitle: TextView
    private lateinit var textViewSum: TextView
    private lateinit var textViewIsSoldOutTitle: TextView
    private lateinit var textViewIsSoldOut: TextView
    private lateinit var textViewCommentTitle: TextView
    private lateinit var textViewComment: TextView
    private lateinit var buttonDel: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonCancel: Button
    private lateinit var buttonOk: Button
    private lateinit var textViewCurrSort: TextView

    private var currentIdForSort: Int = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        val view: View = inflater!!.inflate(R.layout.tour_details, container, false)
        textViewNameTitle = view.findViewById(R.id.textViewExamNameTitle)
        textViewName = view.findViewById(R.id.textViewExamName)
        textViewCountryTitle = view.findViewById(R.id.textViewTeacherNameTitle)
        textViewCountry = view.findViewById(R.id.textViewTeacherName)
        textViewNumTitle = view.findViewById(R.id.textViewAuditoryTitle)
        textViewNum = view.findViewById(R.id.textViewAuditory)
        textViewDateOfSaleTitle = view.findViewById(R.id.textViewDateTitle)
        textViewDateOfSale = view.findViewById(R.id.textViewDate)
        textViewDateStartTitle = view.findViewById(R.id.textViewTimeTitle)
        textViewDateStart = view.findViewById(R.id.textViewTime)
        textViewSumTitle = view.findViewById(R.id.textViewPeopleTitle)
        textViewSum = view.findViewById(R.id.textViewPeople)
        textViewIsSoldOutTitle = view.findViewById(R.id.textViewAbstractTitle)
        textViewIsSoldOut = view.findViewById(R.id.textViewAbstract)
        textViewCommentTitle = view.findViewById(R.id.textViewCommentTitle)
        textViewComment = view.findViewById(R.id.textViewComment)
        buttonDel = view.findViewById(R.id.button_details_delete)
        buttonEdit = view.findViewById(R.id.button_details_edit)
        buttonCancel = view.findViewById(R.id.button_cancel)
        buttonOk = view.findViewById(R.id.button_details_ok)
        textViewCurrSort = view.findViewById(R.id.textViewCurrentSort)

        textViewNameTitle.setOnLongClickListener { setSortId(0) }
        textViewName.setOnLongClickListener { setSortId(0) }
        textViewCountryTitle.setOnLongClickListener { setSortId(1) }
        textViewCountry.setOnLongClickListener { setSortId(1) }
        textViewNumTitle.setOnLongClickListener { setSortId(2) }
        textViewNum.setOnLongClickListener { setSortId(2) }
        textViewDateOfSaleTitle.setOnLongClickListener { setSortId(3) }
        textViewDateOfSale.setOnLongClickListener { setSortId(3) }
        textViewDateStartTitle.setOnLongClickListener { setSortId(4) }
        textViewDateStart.setOnLongClickListener { setSortId(4) }
        textViewSumTitle.setOnLongClickListener { setSortId(5) }
        textViewSum.setOnLongClickListener { setSortId(5) }
        textViewIsSoldOutTitle.setOnLongClickListener { setSortId(6) }
        textViewIsSoldOut.setOnLongClickListener { setSortId(6) }
        textViewCommentTitle.setOnLongClickListener { setSortId(7) }
        textViewComment.setOnLongClickListener { setSortId(7) }

        buttonDel.setOnClickListener { returnDel() }
        buttonEdit.setOnClickListener { returnEdit() }
        buttonCancel.setOnClickListener { dialog.dismiss() }
        buttonOk.setOnClickListener { returnIdForSort() }

        val arguments: Bundle = getArguments()
        textViewName.text = arguments.getString("name")
        textViewCountry.text = arguments.getString("country")
        textViewNum.text = arguments.getString("number")
        textViewDateOfSale.text = arguments.getString("dateOfSale")
        textViewDateStart.text = arguments.getString("dateStart")
        textViewSum.text = arguments.getString("sum")
        if (arguments.getString("isSoldOut") == "1")
        {
            textViewIsSoldOut.text = "да"
        }
        else
        {
            textViewIsSoldOut.text = "нет"
        }
        textViewComment.text = arguments.getString("comment")
        if (arguments.getString("connection") != "1")
        {
            buttonDel.isEnabled = false
            buttonEdit.isEnabled = false
        }

        return view
    }

    override fun onAttach(activity: Activity?)
    {
        super.onAttach(activity)
        try {
            onInputListenerSortId = getActivity() as OnInputListenerSortId
        }
        catch (e: ClassCastException)
        {
            Log.e(exceptionTag, "onAttach: ClassCastException: " + e.message)
        }
    }

    private fun setSortId(id: Int): Boolean
    {
        currentIdForSort = id
        if (currentIdForSort == 0)
        {
            textViewCurrSort.text = "Сортировка по названию"
        }
        else if (currentIdForSort == 1)
        {
            textViewCurrSort.text = "Сортировка по стране"
        }
        else if (currentIdForSort == 2)
        {
            textViewCurrSort.text = "Сортировка по количеству"
        }
        else if (currentIdForSort == 3)
        {
            textViewCurrSort.text = "Сортировка по дате продажи"
        }
        else if (currentIdForSort == 4)
        {
            textViewCurrSort.text = "Сортировка по дате вылета"
        }
        else if (currentIdForSort == 5)
        {
            textViewCurrSort.text = "Сортировка по общей сумме"
        }
        else if (currentIdForSort == 6)
        {
            textViewCurrSort.text = "Сортировка по распроданности"
        }
        else if (currentIdForSort == 7)
        {
            textViewCurrSort.text = "Сортировка по описанию"
        }
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200
            , VibrationEffect.DEFAULT_AMPLITUDE))
        return true
    }

    private fun returnIdForSort()
    {
        onInputListenerSortId.sendInputSortId(currentIdForSort)
        dialog.dismiss()
    }

    private fun returnDel()
    {
        currentIdForSort = 8
        returnIdForSort()
    }

    private fun returnEdit()
    {
        currentIdForSort = 9
        returnIdForSort()
    }
}