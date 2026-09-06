package com.example.myjobseeker.ui.detail

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myjobseeker.R
import com.example.myjobseeker.model.Job

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val job = intent.getSerializableExtra("EXTRA_JOB") as? Job

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        job?.let {
            findViewById<TextView>(R.id.detail_title).text = it.title
            findViewById<TextView>(R.id.detail_company).text = it.companyName
            findViewById<TextView>(R.id.detail_distance).text = getString(R.string.distance_near_format, it.distance)
            findViewById<TextView>(R.id.detail_salary).text = it.salary
            findViewById<TextView>(R.id.detail_match).text = getString(R.string.match_format, it.matchPercentage)
        }
    }
}