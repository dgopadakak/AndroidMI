package com.example.androidmi.firms

data class Tour(
    val name: String,           // Название
    val country: String,        // Страна
    val num: Int,               // Количество проданных туров
    val dateOfSale: String,     // Дата продажи
    val dateStart: String,      // Дата начала тура
    val sum: Int,               // Сумма (руб.)
    val isSoldOut: Int,         // Полностью ли выкуплены
    val comment: String         // Описание тура
)
