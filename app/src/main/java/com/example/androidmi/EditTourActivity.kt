package com.example.androidmi

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class EditTourActivity : AppCompatActivity()
{
    private lateinit var editName: EditText
    private lateinit var editCountry: EditText
    private lateinit var editNumber: EditText
    private lateinit var editDateOfSale: EditText
    private lateinit var editDateStart: EditText
    private lateinit var editSum: EditText
    private lateinit var editIsSoldOut: EditText
    private lateinit var editComment: EditText

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tour)

        editName = findViewById(R.id.editTextExamName)
        editCountry = findViewById(R.id.editTextTeacherName)
        editNumber = findViewById(R.id.editTextAuditory)
        editDateOfSale = findViewById(R.id.editTextDate)
        editDateStart = findViewById(R.id.editTextTime)
        editSum = findViewById(R.id.editTextPeople)
        editIsSoldOut = findViewById(R.id.editTextAbstract)
        editComment = findViewById(R.id.editTextComment)

        val action = intent.getIntExtra("action", 1)

        findViewById<Button>(R.id.button_confirm).setOnClickListener { confirmChanges(action) }

        if (action == 2)
        {
            editName.setText(intent.getStringExtra("name") as String)
            editCountry.setText(intent.getStringExtra("country") as String)
            editNumber.setText(intent.getStringExtra("number") as String)
            editDateOfSale.setText(intent.getStringExtra("dateOfSale") as String)
            editDateStart.setText(intent.getStringExtra("dateStart") as String)
            editSum.setText(intent.getStringExtra("sum") as String)
            if (intent.getStringExtra("isSoldOut") as String == "1")
            {
                editIsSoldOut.setText("да")
            }
            else
            {
                editIsSoldOut.setText("нет")
            }
            editComment.setText(intent.getStringExtra("comment") as String)
        }
    }

    private fun confirmChanges(action: Int)
    {
        if (editName.text.toString() != "" && editCountry.text.toString() != ""
            && editNumber.text.toString() != "" && editDateOfSale.text.toString() != ""
            && editDateStart.text.toString() != "" && editSum.text.toString() != ""
            && editIsSoldOut.text.toString() != "")
        {
            if (editIsSoldOut.text.toString().trim().lowercase(Locale.ROOT) == "да"
                || editIsSoldOut.text.toString().trim().lowercase(Locale.ROOT) == "нет")
            {
                if (isDateValid(editDateOfSale.text.toString().trim())
                    && isDateValid(editDateStart.text.toString().trim()))
                {
                    val intent = Intent(this@EditTourActivity,
                        MainActivity::class.java)
                    intent.putExtra("action",       action)
                    intent.putExtra("name",         editName.text.toString().trim())
                    intent.putExtra("country",      editCountry.text.toString().trim())
                    intent.putExtra("number",       editNumber.text.toString().trim().toInt())
                    intent.putExtra("dateOfSale",   editDateOfSale.text.toString().trim())
                    intent.putExtra("dateStart",    editDateStart.text.toString().trim())
                    intent.putExtra("sum",      editSum.text.toString().trim().toInt())
                    if (editIsSoldOut.text.toString().trim().lowercase(Locale.ROOT) == "да")
                    {
                        intent.putExtra("isSoldOut", 1)
                    }
                    else
                    {
                        intent.putExtra("isSoldOut", 0)
                    }
                    intent.putExtra("comment", editComment.text.toString().trim())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else
                {
                    Snackbar.make(findViewById(R.id.button_confirm),
                        "Проверьте даты!", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
            else
            {
                Snackbar.make(findViewById(R.id.button_confirm),
                    "Поле \"Распродано\" поддерживает только " +
                            "значения \"да\" или \"нет\"!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
            }
        }
        else
        {
            Snackbar.make(findViewById(R.id.button_confirm),
                "Заполните обязательные поля!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.RED)
                .show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isDateValid(date: String?): Boolean
    {
        val myFormat = SimpleDateFormat("dd.MM.yyyy")
        myFormat.isLenient = false
        return try
        {
            if (date != null)
            {
                myFormat.parse(date)
            }
            true
        }
        catch (e: Exception)
        {
            false
        }
    }
}