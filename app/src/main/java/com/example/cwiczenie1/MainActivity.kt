package com.example.cwiczenie1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var workingsTV: TextView
    private lateinit var resultsTV: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun allClearAction(view: View) {
        workingsTV = findViewById(R.id.workingsTV);
        workingsTV.text = ""
        resultsTV = findViewById(R.id.resultsTV);
        resultsTV.text = ""
    }

    fun backspaceAction(view: View) {
        workingsTV = findViewById(R.id.workingsTV);
        var workings: String = workingsTV.text.toString()
        if (workings.isNotEmpty()) {
            workingsTV.text = workings.substring(0, workings.length - 1)
        }
        resultsTV = findViewById(R.id.resultsTV);
        resultsTV.text = ""
    }

    fun operationAction(view: View) {
        
    }

    fun equalsAction(view: View) {
        println("Equals")
    }
}