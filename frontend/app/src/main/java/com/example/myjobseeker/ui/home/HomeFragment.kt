package com.example.myjobseeker.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myjobseeker.R
import com.example.myjobseeker.adapter.JobAdapter
import com.example.myjobseeker.databinding.FragmentHomeBinding
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.detail.DetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var voiceRecognitionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        voiceRecognitionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!results.isNullOrEmpty()) {
                    binding.etSearch.setText(results[0])
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        binding.rvJobs.layoutManager = LinearLayoutManager(context)

        val jobs = listOf(
            Job(1, "Barista", "Kopi Kenangan", "Khatib Sulaiman", 99, "1.8 km", "Rp. 70.000/hari", "15.00 - 22.00", "Basic Coffee"),
            Job(2, "Admin Toko", "Toko Fotocopy", "Limau Manis", 70, "7.5 km", "Rp. 100.000/mg", "19.00 - 21.00", "Printing, dll"),
            Job(3, "Social Media Designer", "Kopi Kenangan", "Khatib Sulaiman", 75, "1.8 km", "Rp. 50.000/design", "free time", "Design")
        )

        val adapter = JobAdapter(jobs) { job ->
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("EXTRA_JOB", job)
            startActivity(intent)
        }
        binding.rvJobs.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.ivProfile.setOnClickListener {
            // Navigate to Profile tab in BottomNavigationView
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_profile
        }

        binding.ivMic.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan bicara...")
        }
        try {
            voiceRecognitionLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Voice recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
