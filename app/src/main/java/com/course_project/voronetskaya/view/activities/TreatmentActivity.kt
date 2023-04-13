package com.course_project.voronetskaya.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Treatment
import com.course_project.voronetskaya.data.model.TreatmentTime
import com.course_project.voronetskaya.view.model.TreatmentsViewModel
import com.course_project.voronetskaya.view.OrganizerApplication

class TreatmentActivity : AppCompatActivity() {
    private lateinit var viewModel: TreatmentsViewModel
    private lateinit var navController: NavController
    private lateinit var statsIcon: ImageView
    private lateinit var homeIcon: ImageView
    private lateinit var kitIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TreatmentsViewModel::class.java]
        setContentView(R.layout.treatment_activity)
        supportActionBar?.hide()

        navController = Navigation.findNavController(this, R.id.f_treatment)

        kitIcon = findViewById(R.id.iv_treatment_kit_icon)
        kitIcon.setOnClickListener {
            val intent = Intent(this, KitActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (OrganizerApplication.sharedPreferences.getString("dependentId", "") != "") {
            kitIcon.visibility = View.INVISIBLE
        }

        homeIcon = findViewById(R.id.iv_treatment_home_icon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        statsIcon = findViewById(R.id.iv_treatment_stats_icon)
        statsIcon.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public fun popStack() {
        navController.popBackStack()
    }

    public fun getViewModel(): TreatmentsViewModel {
        return this.viewModel
    }

    public fun changeActiveTreatment(treatment: Treatment) {
        treatment.setIsActive(!treatment.getIsActive())
        viewModel.updateTreatment(treatment)
    }

    public fun addTreatment(treatment: Treatment) {
        viewModel.addTreatment(treatment)
        val bundle = Bundle()
        bundle.putString("unit", treatment.getUnit())
        navController.navigate(R.id.action_newTreatmentFragment_to_newTreatmentTimeFragment, bundle)
    }

    public fun addTreatmentTime(time: List<TreatmentTime>) {
        viewModel.addTreatmentTime(time, this)
        navController.popBackStack()
        navController.popBackStack()
    }

    public fun showNewTreatment() {
        navController.navigate(R.id.action_treatmentsListFragment_to_newTreatmentFragment)
    }
}