package com.course_project.voronetskaya.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.model.StatsViewModel
import com.course_project.voronetskaya.view.OrganizerApplication

class StatsActivity : AppCompatActivity() {
    private lateinit var pillIcon: ImageView
    private lateinit var homeIcon: ImageView
    private lateinit var kitIcon: ImageView
    private lateinit var navController: NavController

    private lateinit var viewModel: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[StatsViewModel::class.java]
        setContentView(R.layout.stats_activity)
        supportActionBar?.hide()

        navController = Navigation.findNavController(this, R.id.f_stats)
        kitIcon = findViewById(R.id.iv_stats_kit_icon)
        kitIcon.setOnClickListener {
            val intent = Intent(this, KitActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (OrganizerApplication.sharedPreferences.getString("dependentId", "") != "") {
            kitIcon.isClickable = false
        }

        pillIcon = findViewById(R.id.iv_stats_treatment_icon)
        pillIcon.setOnClickListener {
            val intent = Intent(this, TreatmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        homeIcon = findViewById(R.id.iv_stats_home_icon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public fun popStack() {
        navController.popBackStack()
    }

    public fun getViewModel(): StatsViewModel {
        return this.viewModel
    }

    public fun showHistory(date: String) {
        viewModel.setFromDate(date)
        viewModel.setToDate(date)

        navController.navigate(R.id.action_graphStatsFragment_to_historyStatsFragment)
    }
}