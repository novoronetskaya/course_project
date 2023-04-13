@file:Suppress("RedundantVisibilityModifier")

package com.course_project.voronetskaya.view.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.course_project.voronetskaya.data.model.*
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap

class KitActivityViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private var kitsListData: MutableLiveData<List<FirstAidKit>> = MutableLiveData()
    private var invitationsListData: MutableLiveData<List<Invitation>> = MutableLiveData()
    private var medicine: MutableLiveData<Medicine> = MutableLiveData(Medicine())
    private lateinit var medicineId: String
    private var kit: FirstAidKit = FirstAidKit()
    private var kitMedicine: MutableLiveData<List<Medicine>> = MutableLiveData()
    private var independentMembers: MutableLiveData<List<User>> = MutableLiveData()
    private var dependentMembers: MutableLiveData<List<User>> = MutableLiveData()
    private val executor = Executors.newSingleThreadExecutor()
    private var currentUser = User()
    private var uid = ""
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")
    private var fromDate: String = ""
    private var toDate: String = ""
    private var medRefills: MutableLiveData<List<MedicineRefill>> = MutableLiveData()

    init {
        if (OrganizerApplication.auth.currentUser == null) {
            uid = OrganizerApplication.sharedPreferences.getString("uid", "")!!
            executor.execute {
                currentUser =
                    OrganizerApplication.getInstance().getDatabase().userDao().getIndependentUser()
            }
        } else {
            uid = OrganizerApplication.auth.currentUser!!.uid
            firestore.collection("users").whereEqualTo("id", uid).get().addOnSuccessListener {
                currentUser = it.documents[0].toObject(User::class.java)!!
            }
        }

        uploadKits()
        uploadInvitations()
    }

    public fun isAdmin(): Boolean {
        return uid.equals(kit.getAdminId())
    }

    public fun getUserId(): String {
        return uid
    }

    private fun uploadCurrentUser() {
        if (OrganizerApplication.auth.currentUser != null) {

        }

    }

    private fun uploadKits() {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("kit_users").whereEqualTo("userId", uid).addSnapshotListener(
                EventListener { value, e ->
                    if (e != null) {
                        Log.w("Kits list listener", e)
                        return@EventListener
                    }

                    val kitsList: MutableList<FirstAidKit> = mutableListOf()
                    var counter = AtomicInteger(0)
                    for (document in value!!) {
                        val kitUsers = document.toObject(KitUsers::class.java)
                        firestore.collection("kits").whereEqualTo("id", kitUsers.getKitId())
                            .addSnapshotListener { valueKit, _ ->
                                for (doc in valueKit!!) {
                                    counter.incrementAndGet()
                                    kitsList.add(doc.toObject(FirstAidKit::class.java))
                                }

                                if (counter.get() == value.size()) {
                                    kitsListData.value = kitsList
                                }
                            }
                    }
                }
            )
        }
    }

    private fun uploadInvitations() {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("invitations").whereEqualTo("userId", uid).addSnapshotListener(
                EventListener { value, e ->
                    if (e != null) {
                        Log.w("Invitations listener", e)
                        return@EventListener
                    }

                    val invitations: MutableList<Invitation> = mutableListOf()
                    for (document in value!!) {
                        val invitation = document.toObject(Invitation::class.java)
                        invitations.add(invitation)
                    }

                    invitationsListData.value = invitations
                }
            )
        }
    }

    private fun uploadMedicine() {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("medicine").whereEqualTo("kitId", kit.getId()).addSnapshotListener(
                EventListener { value, e ->
                    if (e != null) {
                        Log.w("Medicine list listener", e)
                        return@EventListener
                    }

                    val medicineList: MutableList<Medicine> = mutableListOf()
                    for (document in value!!) {
                        val medicine = document.toObject(Medicine::class.java)
                        medicineList.add(medicine)
                    }

                    kitMedicine.value = medicineList
                }
            )
        }
    }

    public fun chooseMedicine(medicineId: String) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("medicine").whereEqualTo("id", medicineId)
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null || value == null) {
                        return@EventListener
                    }

                    for (document in value) {
                        medicine.value = document.toObject(Medicine::class.java)

                    }
                })
        } else {
            this.medicineId = medicineId
        }
    }

    public fun getMedicine(): LiveData<Medicine> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.medicine
        }
        return OrganizerApplication.getInstance().getDatabase().medicineDao()
            .getMedicineByIdLive(medicineId)
    }

    public fun addKit(name: String) {
        val kit = FirstAidKit()
        kit.setAdminId(uid)
        kit.setName(name)
        kit.setId(UUID.randomUUID().toString())

        val kitUsers = KitUsers()
        kitUsers.setUserId(uid)
        kitUsers.setKitId(kit.getId())

        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("kits").add(kit)
            firestore.collection("kit_users").add(kitUsers)
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().firstAidKitDao().addKit(kit)
                OrganizerApplication.getInstance().getDatabase().kitUsersDao().addKitUser(kitUsers)
            }
        }
    }

    public fun deleteKit(kit: FirstAidKit) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("kit_users").whereEqualTo("userId", uid)
                .whereEqualTo("kitId", kit.getId()).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().firstAidKitDao()
                    .deleteKit(kit)
            }
        }
    }

    public fun acceptInvitation(kitId: String) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("invitations").whereEqualTo("userId", uid)
                .whereEqualTo("kitId", kitId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }

            val data = HashMap<String, String>()
            data["kitId"] = kitId
            data["userId"] = uid
            firestore.collection("kit_users").add(data)
        }
    }

    public fun declineInvitation(kitId: String) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("invitations").whereEqualTo("userId", uid)
                .whereEqualTo("kitId", kitId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
        }
    }

    public fun getKitsList(): LiveData<List<FirstAidKit>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return kitsListData
        }

        return OrganizerApplication.getInstance().getDatabase().firstAidKitDao().getAllKits()
    }

    public fun getInvitationsList(): LiveData<List<Invitation>> {
        return this.invitationsListData
    }

    public fun deleteMedicine(medicine: Medicine) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("medicine").whereEqualTo("id", medicine.getId())
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null) {
                        return@EventListener
                    }

                    for (document in value!!) {
                        document.reference.delete()
                    }
                })
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().medicineDao()
                    .deleteMedicine(medicine)
            }
        }
    }

    public fun setKit(kit: FirstAidKit) {
        this.kit = kit

        uploadMedicine()
        uploadUsers()
    }

    public fun getKit(): FirstAidKit {
        return this.kit
    }

    public fun getMedicineFromKit(): LiveData<List<Medicine>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.kitMedicine
        }
        return OrganizerApplication.getInstance().getDatabase().medicineDao()
            .getMedicineFromKit(kit.getId())
    }

    private fun uploadUsers() {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("kit_users").whereEqualTo("kitId", kit.getId())
                .addSnapshotListener(
                    EventListener { value, e ->
                        if (e != null) {
                            Log.w("Kits members listener", e)
                            return@EventListener
                        }

                        val usersObject: MutableList<User> = mutableListOf()
                        for (document in value!!) {
                            val curUser = document.toObject(User::class.java)
                            curUser.setId(document.id)
                            usersObject.add(curUser)
                        }

                        independentMembers.value = usersObject.filter {
                            it.getIsIndependent()
                        }
                        dependentMembers.value = usersObject.filter {
                            !(it.getIsIndependent())
                        }
                    }
                )
        }
    }

    public fun sendInvitation(sendToId: String, message: String) {
        if (OrganizerApplication.auth.currentUser != null) {
            val invitation = Invitation()
            invitation.setUserId(sendToId)
            invitation.setMessage(message)
            invitation.setInvitedById(currentUser.getId())
            invitation.setInvitedByName(currentUser.getFirstName() + currentUser.getSurname())

            firestore.collection("invitations").add(invitation)
        }
    }

    public fun getIndependentMembers(): LiveData<List<User>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.independentMembers
        }
        return OrganizerApplication.getInstance().getDatabase().userDao()
            .getIndependentMembersFromKit(kit.getId())
    }

    public fun getDependentMembers(): LiveData<List<User>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.dependentMembers
        }
        return OrganizerApplication.getInstance().getDatabase().userDao()
            .getDependentMembersFromKit(kit.getId())
    }

    public fun deleteKitMember(member: User) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("kit_users").whereEqualTo("kitId", kit.getId())
                .whereEqualTo("userId", member.getId()).get().addOnSuccessListener { documents ->
                    for (document in documents.documents) {
                        document.reference.delete()
                    }
                }

            if (!member.getIsIndependent()) {
                OrganizerApplication.getInstance().deleteAccount(member.getId())
            }
        } else {
            executor.execute {
                val kitUsers = KitUsers()
                kitUsers.setUserId(member.getId())
                kitUsers.setKitId(kit.getId())

                OrganizerApplication.getInstance().getDatabase().kitUsersDao()
                    .deleteKitUser(kitUsers)
                OrganizerApplication.getInstance().getDatabase().userDao().deleteUser(member)
            }
        }
    }

    public fun addDependentMember(name: String, birthday: String) {
        val user = User()
        user.setId(UUID.randomUUID().toString())
        user.setFirstName(name)
        user.setBirthday(birthday)
        user.setIsIndependent(false)

        val kitUser = KitUsers()
        kitUser.setUserId(user.getId())
        kitUser.setKitId(kit.getId())

        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("users").add(user)
            firestore.collection("kit_users").add(kitUser)
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().userDao().addUser(user)
                OrganizerApplication.getInstance().getDatabase().kitUsersDao().addKitUser(kitUser)
            }
        }
    }

    public fun updateMedicine(medicine: Medicine) {
        if (OrganizerApplication.auth.currentUser != null) {
            val data = HashMap<String, Any>()
            data["expirationDate"] = medicine.getExpirationDate()
            data["producer"] = medicine.getProducer()
            data["pharmEffect"] = medicine.getPharmEffect()
            data["symptom"] = medicine.getSymptom()
            data["name"] = medicine.getName()
            firestore.collection("medicine").whereEqualTo("id", medicine.getId())
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null) {
                        return@EventListener
                    }

                    for (document in value!!) {
                        document.reference.update(data)
                    }
                })
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().medicineDao()
                    .updateMedicine(medicine)
            }
        }
    }

    public fun addMedicine(medicine: Medicine) {
        medicine.setKitId(kit.getId())
        medicine.setId(UUID.randomUUID().toString())

        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("medicine").add(medicine)
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().medicineDao().addMedicine(medicine)
            }
        }
    }

    public fun addRefill(medicine: Medicine, amount: Int) {
        if (OrganizerApplication.auth.currentUser != null) {
            val refill = MedicineRefill()
            refill.setMedicineId(medicine.getId())
            refill.setAmount(amount)
            refill.setDate(dateFormatter.format(Date()))

            refill.setUserId(OrganizerApplication.auth.currentUser!!.uid)
            firestore.collection("medicine_refills").add(refill)

            firestore.collection("medicine").whereEqualTo("id", medicine.getId())
                .addSnapshotListener { value, _ ->
                    for (document in value!!) {
                        document.reference.update("amount", medicine.getAmount() + amount)
                    }
                }
        } else {
            executor.execute {
                val userId =
                    OrganizerApplication.getInstance().getDatabase().userDao().getIndependentUser()
                        .getId()

                val refill = MedicineRefill()
                refill.setMedicineId(medicine.getId())
                refill.setAmount(amount)
                refill.setUserId(userId)
                refill.setDate(dateFormatter.format(Date()))
                refill.setId(UUID.randomUUID().toString())

                OrganizerApplication.getInstance().getDatabase().medicineRefillsDao()
                    .addMedicineRefill(refill)
                medicine.setAmount(medicine.getAmount() + amount)
                OrganizerApplication.getInstance().getDatabase().medicineDao()
                    .updateMedicine(medicine)
            }
        }
    }

    public fun setFromDate(date: String) {
        this.fromDate = date

        if (OrganizerApplication.auth.currentUser != null) {
            updateRefillsFirestore()
        }
    }

    public fun setToDate(date: String) {
        this.toDate = date

        if (OrganizerApplication.auth.currentUser != null) {
            updateRefillsFirestore()
        }
    }

    private fun updateRefillsFirestore() {
        firestore.collection("medicine_refills")
            .whereEqualTo("medicineId", medicine.value!!.getId())
            .whereGreaterThanOrEqualTo("date", this.fromDate)
            .whereLessThanOrEqualTo("date", this.toDate).addSnapshotListener(
                EventListener { value, e ->
                    if (e != null) {
                        Log.w("Refills listener", e)
                        return@EventListener
                    }

                    val refills: MutableList<MedicineRefill> = mutableListOf()
                    for (document in value!!) {
                        refills.add(document.toObject(MedicineRefill::class.java))
                    }

                    this.medRefills.value = refills
                })
    }

    public fun getRefills(): LiveData<List<MedicineRefill>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.medRefills
        }

        return OrganizerApplication.getInstance().getDatabase().medicineRefillsDao()
            .getBetweenDates(fromDate, toDate, medicineId)
    }
}