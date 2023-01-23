package com.example.androidmi

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialogFragmentDelTour: DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val arguments: Bundle? = arguments
        val examName = arguments?.getString("name")
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Будет удален тур: $examName")
            .setTitle("Внимание!")
            .setPositiveButton("Продолжить"
            ) { _, _ -> (activity as MainActivity?)?.delTour() }
            .setNegativeButton("Отмена") { _, _ -> }
        return builder.create()
    }
}