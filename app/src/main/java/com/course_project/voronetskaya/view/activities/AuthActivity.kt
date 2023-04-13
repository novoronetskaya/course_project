package com.course_project.voronetskaya.view.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.User
import com.course_project.voronetskaya.view.OrganizerApplication
import java.util.*
import java.util.concurrent.Executors

class AuthActivity : AppCompatActivity() {
    private val guestSettings = "GuestSignedIn"
    private lateinit var preferences: SharedPreferences
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)
        supportActionBar?.hide()
        navController = Navigation.findNavController(this, R.id.f_auth)
        preferences = OrganizerApplication.sharedPreferences

        if (preferences.getBoolean(
                guestSettings,
                false
            ) || OrganizerApplication.auth.currentUser != null
        ) {
            navController.navigate(R.id.mainActivity)
        }
    }

    public fun signInAsGuest() {
        preferences.edit().putBoolean(guestSettings, true).apply()

        navController.navigate(R.id.action_welcomeFragment_to_mainActivity)
        val user = User()
        user.setIsIndependent(true)
        user.setId(UUID.randomUUID().toString())

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            OrganizerApplication.getInstance().getDatabase().userDao().addUser(user)
            preferences.edit().putString("uid", user.getId()).apply()
        }
    }
}