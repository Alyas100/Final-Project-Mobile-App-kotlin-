package com.example.finalprojectmobileapp.ui.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.data.models.Reminder
import com.example.finalprojectmobileapp.ui.activities.local_notifications.ReminderUtils

class ReminderAdapter(private val context: Context) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var remindersList: List<Reminder> = emptyList()
    private val reminderUtils = ReminderUtils()

    // Method to update the list of reminders
    fun setReminders(reminders: List<Reminder>) {
        this.remindersList = reminders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = remindersList[position]

        // Bind the reminder data to the view
        holder.reminderTitleTextView.text = reminder.activity
        holder.reminderTimeTextView.text = reminder.time

        // Set the Switch state based on the reminder data
        holder.reminderSwitch.isChecked = reminder.isEnabled

        holder.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Update the Reminder object to reflect the change
            reminder.isEnabled = isChecked

            // Schedule or cancel the reminder based on the switch state
            if (isChecked) {
                reminderUtils.scheduleReminder(context, reminder)
            } else {
                reminderUtils.cancelReminder(context, reminder)
            }

            // Ensure the data change happens after layout pass
            holder.itemView.post {
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return remindersList.size
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderTitleTextView: TextView = itemView.findViewById(R.id.reminderTitleTextView)
        val reminderTimeTextView: TextView = itemView.findViewById(R.id.reminderTimeTextView)
        val reminderSwitch: Switch = itemView.findViewById(R.id.reminderSwitch)
    }
}
