package com.example.myjobseeker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myjobseeker.R
import com.example.myjobseeker.model.Job

class JobAdapter(
    private val jobs: List<Job>,
    private val onItemClick: (Job) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.job_title)
        val company: TextView = view.findViewById(R.id.company_info)
        val salary: TextView = view.findViewById(R.id.tv_salary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_card, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]
        val context = holder.itemView.context
        holder.title.text = job.title
        holder.company.text = context.getString(R.string.company_location_format, job.companyName, job.location)
        holder.salary.text = job.salary

        holder.itemView.setOnClickListener { onItemClick(job) }
    }

    override fun getItemCount() = jobs.size
}