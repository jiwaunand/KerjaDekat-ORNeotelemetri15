package com.example.myjobseeker.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myjobseeker.MainActivity
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
            findViewById<TextView>(R.id.detail_location).text = it.location
            findViewById<TextView>(R.id.detail_salary).text = it.salary
            findViewById<TextView>(R.id.detail_description).text = it.description
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.iv_profile).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("NAVIGATE_TO", R.id.nav_profile)
            startActivity(intent)
            finish()
        }
    }
}
