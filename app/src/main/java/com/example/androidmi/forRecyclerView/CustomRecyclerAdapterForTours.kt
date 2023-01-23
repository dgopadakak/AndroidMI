package com.example.androidmi.forRecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmi.R

class CustomRecyclerAdapterForExams(private val names: List<String>,
                                    private val countries: List<String>,
                                    private val numbers: List<Int>,
                                    private val years: List<String>,
                                    private val year: String):
    RecyclerView.Adapter<CustomRecyclerAdapterForExams.MyViewHolder>()
{
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val layoutItem: ConstraintLayout = itemView.findViewById(R.id.layoutItem)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewAddress: TextView = itemView.findViewById(R.id.textViewAddress)
        val textViewNum: TextView = itemView.findViewById(R.id.textViewNum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.textViewName.text = names[position]
        holder.textViewAddress.text = countries[position]
        holder.textViewNum.text = numbers[position].toString()
        if (years[position] != year && year != "Все годы")
        {
            holder.layoutItem.visibility = View.GONE
            holder.layoutItem.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    override fun getItemCount() = names.size
}