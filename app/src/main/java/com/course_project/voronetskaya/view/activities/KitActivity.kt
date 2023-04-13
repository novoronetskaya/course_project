package com.course_project.voronetskaya.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.FirstAidKit
import com.course_project.voronetskaya.data.model.Invitation
import com.course_project.voronetskaya.data.model.Medicine
import com.course_project.voronetskaya.data.model.User
import com.course_project.voronetskaya.view.model.KitActivityViewModel
import com.course_project.voronetskaya.view.OrganizerApplication

class KitActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var viewModel: KitActivityViewModel
    private lateinit var homeIcon: ImageView
    private lateinit var statsIcon: ImageView
    private lateinit var pillIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[KitActivityViewModel::class.java]
        setContentView(R.layout.kit_activity)
        supportActionBar?.hide()

        navController = Navigation.findNavController(this, R.id.f_kit)

        homeIcon = findViewById(R.id.iv_kit_home_icon)
        statsIcon = findViewById(R.id.iv_kit_stats_icon)

        homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        pillIcon = findViewById(R.id.iv_kit_treatment_icon)
        pillIcon.setOnClickListener {
            val intent = Intent(this, TreatmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        statsIcon = findViewById(R.id.iv_kit_stats_icon)
        statsIcon.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public fun showKitInfo(kit: FirstAidKit) {
        viewModel.setKit(kit)
        val bundle = Bundle()
        bundle.putString("kitId", kit.getId())
        bundle.putString("kitName",kit.getName())
        navController.navigate(R.id.action_kitsListFragment_to_medicineListFragment, bundle)
    }

    public fun showInvitations() {
        navController.navigate(R.id.action_kitsListFragment_to_invitationsListFragment)
    }

    public fun showInvitationInfo(invitation: Invitation) {
        val bundle = Bundle()
        bundle.putString("kitName", invitation.getKitName())
        bundle.putString("invitedById", invitation.getInvitedById())
        bundle.putString("invitedByName", invitation.getInvitedByName())
        bundle.putString("message", invitation.getMessage())
        bundle.putString("kitId", invitation.getKitId())

        navController.navigate(
            R.id.action_invitationsListFragment_to_invitationInfoFragment,
            bundle
        )
    }

    public fun deleteKit(kit: FirstAidKit) {
        viewModel.deleteKit(kit)
    }

    public fun acceptInvitation(kitId: String) {
        navController.popBackStack()
        viewModel.acceptInvitation(kitId)
    }

    public fun declineInvitation(kitId: String) {
        navController.popBackStack()
        viewModel.declineInvitation(kitId)
    }

    public fun popStack() {
        navController.popBackStack()
    }

    public fun createNewKit(name: String) {
        viewModel.addKit(name)
    }

    public fun getViewModel(): KitActivityViewModel {
        return this.viewModel
    }

    public fun showMedicineInfo(medicine: Medicine) {
        viewModel.chooseMedicine(medicine.getId())

        val bundle = Bundle()
        bundle.putString("kitName", viewModel.getKit().getName())
        navController.navigate(R.id.action_medicineListFragment_to_medicineInfoFragment, bundle)
    }

    public fun deleteMedicine(medicine: Medicine) {
        viewModel.deleteMedicine(medicine)
    }

    public fun showAddMedicineFragment() {
        val bundle = Bundle()
        bundle.putString("kitName", viewModel.getKit().getName())

        navController.navigate(R.id.action_medicineListFragment_to_newMedicineFragment, bundle)
    }

    public fun showMembers() {
        val bundle = Bundle()
        bundle.putBoolean("isAdmin", viewModel.isAdmin())
        bundle.putString("userId", viewModel.getUserId())
        navController.navigate(R.id.action_medicineListFragment_to_kitUsersListFragment, bundle)
    }

    public fun sendInvitation(sendToId: String, message: String) {
        viewModel.sendInvitation(sendToId, message)
    }

    public fun deleteKitMember(member: User) {
        viewModel.deleteKitMember(member)
    }

    public fun addDependentMember(name: String, birthday: String) {
        viewModel.addDependentMember(name, birthday)
        navController.popBackStack()
    }

    public fun showAddIndependentUser() {
        val bundle = Bundle()
        bundle.putString("kitName", viewModel.getKit().getName())
        navController.navigate(R.id.action_kitUsersListFragment_to_inviteUserFragment, bundle)
    }

    public fun showAddDependentUser() {
        val bundle = Bundle()
        bundle.putString("kitName", viewModel.getKit().getName())
        navController.navigate(R.id.action_kitUsersListFragment_to_addDependentUserFragment, bundle)
    }

    public fun showRefillsHistory(medId: String, medName: String) {
        viewModel.chooseMedicine(medId)
        val bundle = Bundle()
        bundle.putString("kitName", viewModel.getKit().getName())
        bundle.putString("medicineName", medName)
        navController.navigate(R.id.action_medicineInfoFragment_to_medicineRefillsFragment, bundle)
    }

    public fun updateMedicine(medicine: Medicine) {
        viewModel.updateMedicine(medicine)
        navController.popBackStack()
    }

    public fun addRefill(medicine: Medicine, refillAmount: Int) {
        viewModel.addRefill(medicine, refillAmount)
    }

    public fun addMedicine(medicine: Medicine) {
        viewModel.addMedicine(medicine)
        popStack()
    }

    public fun showDependentUserProfile(user: User) {
        OrganizerApplication.sharedPreferences.edit().putString("dependentId", user.getId()).apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}