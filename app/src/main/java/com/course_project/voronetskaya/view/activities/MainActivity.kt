package com.course_project.voronetskaya.view.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.model.MainViewModel
import com.course_project.voronetskaya.view.model.TreatmentWithTime
import com.course_project.voronetskaya.view.OrganizerApplication
import com.course_project.voronetskaya.receivers.UploadBroadcastReceiver
import java.util.*

class MainActivity : AppCompatActivity() {
    private val confirmationCodeKey = "ConfirmationCode"
    private lateinit var preferences: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel

    private lateinit var kitIcon: ImageView
    private lateinit var manager: AlarmManager
    private lateinit var pillIcon: ImageView
    private lateinit var statsIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        navController = Navigation.findNavController(this, R.id.f_main)

        if (intent.getStringExtra("signUp") != null) {
            navController.popBackStack()
            navController.navigate(R.id.signUpEmailFragment)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notChan = NotificationChannel("1", "CHANNEL", NotificationManager.IMPORTANCE_HIGH)
            val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(notChan)
        }

        setDailyUpload()
        preferences = OrganizerApplication.sharedPreferences
        navController = Navigation.findNavController(this, R.id.f_main)

        kitIcon = findViewById(R.id.iv_main_kit_icon)
        kitIcon.setOnClickListener {
            val intent = Intent(this, KitActivity::class.java)
            startActivity(intent)
            finish()
        }

        pillIcon = findViewById(R.id.iv_main_treatment_icon)
        pillIcon.setOnClickListener {
            val intent = Intent(this, TreatmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        statsIcon = findViewById(R.id.iv_main_stats_icon)
        statsIcon.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (OrganizerApplication.auth.currentUser != null) {
            viewModel.setUser(OrganizerApplication.auth.currentUser!!.uid)
        }

        if (OrganizerApplication.sharedPreferences.getString("dependentId", "") != "") {
            kitIcon.isClickable = false
        }
    }

    public fun finishDependentUser() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    public fun setCodePreferences(code: String) {
        preferences.edit().putString(confirmationCodeKey, code).apply()
    }

    public fun showEnterCodeFragment(email: String) {
        val bundle = Bundle()
        bundle.putString("email", email)
        bundle.putString("code", preferences.getString(confirmationCodeKey, "")!!)
        navController.navigate(R.id.action_signUpEmailFragment_to_signUpCodeFragment, bundle)
    }

    public fun showEnterInfoFragment() {
        val bundle = Bundle()
        bundle.putString("email", viewModel.getSignUpEmail())
        navController.navigate(R.id.action_signUpCodeFragment_to_signUpInfoFragment, bundle)
    }

    public fun popStack() {
        navController.popBackStack()
    }

    public fun endRegistration() {
        if (OrganizerApplication.sharedPreferences.getBoolean("GuestSignedIn", false)) {
            OrganizerApplication.sharedPreferences.edit().remove("GuestSignedIn").apply()

            viewModel.synchronizeData()
        }

        navController.navigate(R.id.timelineFragment)
        viewModel.setUser(OrganizerApplication.auth.currentUser!!.uid)
        while (navController.popBackStack()) {
        }
    }

    public fun showProfile() {
        if (OrganizerApplication.auth.currentUser != null) {
            navController.navigate(R.id.registeredProfileFragment)
        } else {
            navController.navigate(R.id.action_timelineFragment_to_nonRegisteredProfileFragment)
        }
    }

    public fun showOneTimeTreatmentFragment() {
        navController.navigate(R.id.action_timelineFragment_to_oneTimeTreatmentFragment)
    }

    public fun registerUser() {
        navController.navigate(R.id.action_nonRegisteredProfileFragment_to_signUpEmailFragment)
    }

    public fun logOut() {
        OrganizerApplication.auth.signOut()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    public fun deleteAccount() {
        val uid = OrganizerApplication.auth.currentUser!!.uid
        val user = OrganizerApplication.auth.currentUser

        user!!.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModel.deleteAccount(uid)
                logOut()
            } else {
                Toast.makeText(
                    this,
                    "Возникла ошибка. Войдите и попробуйте снова",
                    Toast.LENGTH_LONG
                ).show()
                logOut()
            }
        }
    }

    public fun changeEmail() {
        navController.navigate(R.id.action_registeredProfileFragment_to_emailResetFragment)
    }

    public fun getViewModel(): MainViewModel {
        return viewModel
    }

    private fun setDailyUpload() {
        manager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(this, UploadBroadcastReceiver::class.java)
        var intent = PendingIntent.getBroadcast(
            this, 0, alarmIntent,
            FLAG_NO_CREATE or FLAG_IMMUTABLE
        )
        if (intent != null) {
            return
        }

        intent = PendingIntent.getBroadcast(
            this, 0, alarmIntent,
            FLAG_IMMUTABLE
        )

        val curDate = Calendar.getInstance()
        curDate.add(Calendar.DAY_OF_MONTH, 1)
        curDate.set(Calendar.HOUR_OF_DAY, 0)
        curDate.set(Calendar.MINUTE, 3)

        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            curDate.timeInMillis,
            intent
        )
    }


    public fun showTreatmentInfo(treatmentWithTime: TreatmentWithTime) {
        viewModel.setTreatmentWithTime(treatmentWithTime)
        navController.navigate(R.id.action_timelineFragment_to_treatmentInfoFragment)
    }
}