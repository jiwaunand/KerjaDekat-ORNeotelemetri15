package com.example.myjobseeker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myjobseeker.R
import com.example.myjobseeker.adapter.JobAdapter
import com.example.myjobseeker.model.Job

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvJobs = view.findViewById<RecyclerView>(R.id.rv_jobs)
        rvJobs.layoutManager = LinearLayoutManager(context)

        val jobs = listOf(
            Job(1, "Barista", "Kopi Kenangan", "Khatib Sulaiman", 99, "1.8 km", "Rp. 70.000/hari", "15.00 - 22.00", "Basic Coffee"),
            Job(2, "Admin Toko", "Toko Fotocopy", "Limau Manis", 70, "7.5 km", "Rp. 100.000/mg", "19.00 - 21.00", "Printing, dll"),
            Job(3, "Social Media Designer", "Kopi Kenangan", "Khatib Sulaiman", 75, "1.8 km", "Rp. 50.000/design", "free time", "Design")
        )

        val adapter = JobAdapter(jobs) { job ->
            val intent = android.content.Intent(context, com.example.myjobseeker.ui.detail.DetailActivity::class.java)
            intent.putExtra("EXTRA_JOB", job)
            startActivity(intent)
        }
        rvJobs.adapter = adapter
    }
}