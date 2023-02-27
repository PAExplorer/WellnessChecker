package com.example.wellnesschecker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateTextView: TextView
    private lateinit var moodRadioGroup: RadioGroup
    private lateinit var saveButton: TextView
    private var selectedMood: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        calendarView = findViewById(R.id.calendar_view)
        selectedDateTextView = findViewById(R.id.selected_date_text_view)
        moodRadioGroup = findViewById(R.id.mood_radio_group)
        saveButton = findViewById(R.id.save_button)

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        //calendar
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateText = "${month+1}/$dayOfMonth/$year"
            selectedDateTextView.text = dateText

            //This spits the value back to the radio buttons
            //This spits the value back to the radio buttons
            selectedMood = sharedPref.getInt(dateText, -1)
            when (selectedMood) {
                1 -> moodRadioGroup.check(R.id.sad_radio_button)
                2 -> moodRadioGroup.check(R.id.neutral_radio_button)
                3 -> moodRadioGroup.check(R.id.happy_radio_button)
                else -> moodRadioGroup.clearCheck()
            }

            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            val millis = cal.timeInMillis
            val dayView = calendarView.getChildAt(0) as ViewGroup
            val dayChildCount = dayView.childCount
            for (i in 0 until dayChildCount) {
                val dayChild = dayView.getChildAt(i)
                if (dayChild is TextView && dayChild.text.isNotEmpty()) {
                    val day = dayChild.text.toString().toInt()
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    val dayMillis = cal.timeInMillis
                    val mood = sharedPref.getInt(SimpleDateFormat("M/d/yyyy").format(dayMillis), -1)
                    when (mood) {
                        1 -> dayChild.setTextColor(ContextCompat.getColor(this, R.color.sad_color))
                        3 -> dayChild.setTextColor(ContextCompat.getColor(this, R.color.happy_color))
                        else -> dayChild.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    }
                }
            }
        }

        //radio buttons
        moodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedMood = when (checkedId) {
                R.id.sad_radio_button -> 1
                R.id.neutral_radio_button -> 2
                R.id.happy_radio_button -> 3
                else -> -1
            }
        }

        //save button
        saveButton.setOnClickListener {
            val dateText = selectedDateTextView.text.toString()
            if (selectedMood == -1) {
                Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            with (sharedPref.edit()) {
                putInt(dateText, selectedMood)
                apply()
            }
            Toast.makeText(this, "Mood saved for $dateText", Toast.LENGTH_SHORT).show()
        }
    }
}