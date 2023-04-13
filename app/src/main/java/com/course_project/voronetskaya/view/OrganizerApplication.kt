@file:Suppress("RedundantVisibilityModifier")

package com.course_project.voronetskaya.view

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.course_project.voronetskaya.data.database.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrganizerApplication : Application() {
    companion object {
        private lateinit var instance: OrganizerApplication
        public lateinit var sharedPreferences: SharedPreferences
        public lateinit var auth: FirebaseAuth
        public fun getInstance(): OrganizerApplication {
            return instance
        }
    }

    private lateinit var database: AppDatabase
    private lateinit var firestore: FirebaseFirestore

    public override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()
        auth = Firebase.auth
        sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE)
        firestore = Firebase.firestore
    }

    public fun getDatabase(): AppDatabase {
        return this.database
    }

    public fun deleteAccount(uid: String) {
        deleteDocumentsWithEqualField("kit_users", "userid", uid)
        deleteDocumentsWithEqualField("invitations", "userid", uid)

        firestore.collection("treatments").whereEqualTo("userId", uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents.documents) {
                    deleteDocumentsWithEqualField("treatment_history", "treatmentId", document.reference.id)
                    deleteDocumentsWithEqualField("treatment_time", "treatmentId", document.reference.id)
                }
            }

        deleteDocumentsWithEqualField("users", "id", uid)
    }

    private fun deleteDocumentsWithEqualField(collection: String, field: String, value: Any) {
        firestore.collection(collection).whereEqualTo(field, value).get()
            .addOnSuccessListener { documents ->
                for (document in documents.documents) {
                    document.reference.delete()
                }
            }
    }
}