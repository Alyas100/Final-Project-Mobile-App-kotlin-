package com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectmobileapp.R

// RecyclerViews need an adapter to bind data to the list items.
// The adapter takes a list of data and inflates a layout for each item.
// It handles how each data item is displayed.
class HistoryAdapter(private val items: List<String>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    // ViewHolder class to hold item views
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHistoryItem: TextView = itemView.findViewById(R.id.tvHistoryItem)
    }

    // Inflates the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    // Binds data to the item views
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.tvHistoryItem.text = items[position]
    }

    // Returns the total number of items
    override fun getItemCount(): Int {
        return items.size
    }
}
