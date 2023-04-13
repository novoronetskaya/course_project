package com.course_project.voronetskaya.view.fragments.treatment_activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.FirstAidKit
import com.course_project.voronetskaya.data.model.Medicine
import com.course_project.voronetskaya.data.model.Treatment
import com.course_project.voronetskaya.data.util.ConsumptionType
import com.course_project.voronetskaya.view.activities.TreatmentActivity
import com.google.common.math.IntMath.pow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewTreatmentFragment : Fragment() {
    private lateinit var medicineName: EditText
    private lateinit var units: EditText
    private lateinit var kitSpinner: Spinner
    private lateinit var medicineNameSpinner: Spinner
    private lateinit var instructionText: EditText
    private lateinit var startDate: EditText
    private lateinit var lengthText: EditText

    private lateinit var eachNDaysText: EditText
    private lateinit var treatmentPeriodCycle: EditText
    private lateinit var pausePeriodCycle: EditText

    private lateinit var mondayBox: CheckBox
    private lateinit var tuesdayBox: CheckBox
    private lateinit var wednesdayBox: CheckBox
    private lateinit var thursdayBox: CheckBox
    private lateinit var fridayBox: CheckBox
    private lateinit var saturdayBox: CheckBox
    private lateinit var sundayBox: CheckBox

    private lateinit var everyDayRadio: RadioButton
    private lateinit var eachNDaysRadio: RadioButton
    private lateinit var daysOfWeekRadio: RadioButton
    private lateinit var cyclicRadio: RadioButton

    private lateinit var nextButton: Button
    private lateinit var cancelButton: Button

    private lateinit var daysArray: Array<CheckBox>

    private lateinit var calendar: Calendar
    private var isKitSpinnerInitialized = false
    private var isMedicineSpinnerInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_treatment_fragment, container, false)

        medicineName = view.findViewById(R.id.et_treatment_medicine_name)
        units = view.findViewById(R.id.et_treatment_medicine_units)
        kitSpinner = view.findViewById(R.id.sp_treatment_default_kit)
        medicineNameSpinner = view.findViewById(R.id.sp_treatment_medicine_name)
        instructionText = view.findViewById(R.id.et_treatment_instruction)
        startDate = view.findViewById(R.id.et_treatment_start_date)
        lengthText = view.findViewById(R.id.et_length)

        everyDayRadio = view.findViewById(R.id.rb_everyday)
        eachNDaysRadio = view.findViewById(R.id.rb_each_n_days)
        daysOfWeekRadio = view.findViewById(R.id.rb_days_of_week)
        cyclicRadio = view.findViewById(R.id.rb_cyclic_treatment)

        eachNDaysText = view.findViewById(R.id.et_number_of_days)
        treatmentPeriodCycle = view.findViewById(R.id.et_number_of_cyclic_treatment_days)
        pausePeriodCycle = view.findViewById(R.id.et_number_of_cyclic_pause_days)

        nextButton = view.findViewById(R.id.b_next)
        cancelButton = view.findViewById(R.id.cancel_button)

        mondayBox = view.findViewById(R.id.cb_monday)
        tuesdayBox = view.findViewById(R.id.cb_tuesday)
        wednesdayBox = view.findViewById(R.id.cb_wednesday)
        thursdayBox = view.findViewById(R.id.cb_thursday)
        fridayBox = view.findViewById(R.id.cb_friday)
        saturdayBox = view.findViewById(R.id.cb_saturday)
        sundayBox = view.findViewById(R.id.cb_sunday)
        daysArray = arrayOf(
            sundayBox,
            mondayBox,
            tuesdayBox,
            wednesdayBox,
            thursdayBox,
            fridayBox,
            saturdayBox
        )

        setKitsObserver()
        handleButtonsClick()

        nextButton.setOnClickListener {
            handleNextButtonClick()
        }

        cancelButton.setOnClickListener {
            (activity as TreatmentActivity).popStack()
        }

        calendar = Calendar.getInstance()
        startDate.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as TreatmentActivity,
                { _, year, month, day ->
                    startDate.setText(String.format("%02d.%02d.%d", day, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = calendar.timeInMillis
            dialog.show()
        }

        kitSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isKitSpinnerInitialized) {
                    isKitSpinnerInitialized = true
                    return
                }

                if (parent == null) {
                    return
                }

                val chosenKit = parent.getItemAtPosition(position) as FirstAidKit
                if (chosenKit.getName() == "Не выбрано") {
                    units.isActivated = true
                    medicineNameSpinner.visibility = View.GONE
                    medicineName.visibility = View.VISIBLE
                } else {
                    units.isActivated = false
                    medicineNameSpinner.visibility = View.VISIBLE
                    medicineName.visibility = View.GONE
                    (activity as TreatmentActivity).getViewModel().setKit(chosenKit)
                    setMedicineSpinnerData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        medicineNameSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isMedicineSpinnerInitialized) {
                    isMedicineSpinnerInitialized = true
                    return
                }

                if (parent == null) {
                    return
                }

                val chosenMedicine = parent.getItemAtPosition(position) as Medicine

                if (chosenMedicine.getName() == "Не выбрано") {
                    return
                }
                units.setText(chosenMedicine.getUnit())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        return view
    }

    private fun setKitsObserver() {
        (activity as TreatmentActivity).getViewModel().getUserKits()
            .observe(viewLifecycleOwner) { kits ->
                val noKit = FirstAidKit()
                noKit.setName("Не выбрано")

                val kitsWithNoKit = ArrayList<FirstAidKit>(kits)
                kitsWithNoKit.add(0, noKit)

                val adapter = ArrayAdapter(
                    activity as TreatmentActivity,
                    android.R.layout.simple_spinner_item,
                    kitsWithNoKit
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                kitSpinner.adapter = adapter
            }
    }

    private fun setMedicineSpinnerData() {
        (activity as TreatmentActivity).getViewModel().getMedicineFromKit()
            .observe(viewLifecycleOwner) { medicine ->
                val noMedicine = Medicine()
                noMedicine.setName("Не выбрано")

                val medicineWithNoMedicine = ArrayList<Medicine>(medicine)
                medicineWithNoMedicine.add(0, noMedicine)

                val adapter = ArrayAdapter(
                    activity as TreatmentActivity,
                    android.R.layout.simple_spinner_item,
                    medicineWithNoMedicine
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                medicineNameSpinner.adapter = adapter
            }
    }

    private fun handleButtonsClick() {
        everyDayRadio.setOnClickListener {
            eachNDaysRadio.isChecked = false
            daysOfWeekRadio.isChecked = false
            cyclicRadio.isChecked = false

            eachNDaysText.isEnabled = false
            treatmentPeriodCycle.isEnabled = false
            pausePeriodCycle.isEnabled = false
            setValueToCheckboxes(false)
        }

        eachNDaysRadio.setOnClickListener {
            everyDayRadio.isChecked = false
            daysOfWeekRadio.isChecked = false
            cyclicRadio.isChecked = false

            eachNDaysText.isEnabled = true
            treatmentPeriodCycle.isEnabled = false
            pausePeriodCycle.isEnabled = false
            setValueToCheckboxes(false)
        }

        daysOfWeekRadio.setOnClickListener {
            everyDayRadio.isChecked = false
            eachNDaysRadio.isChecked = false
            cyclicRadio.isChecked = false

            eachNDaysText.isEnabled = false
            treatmentPeriodCycle.isEnabled = false
            pausePeriodCycle.isEnabled = false
            setValueToCheckboxes(true)
        }

        cyclicRadio.setOnClickListener {
            everyDayRadio.isChecked = false
            eachNDaysRadio.isChecked = false
            daysOfWeekRadio.isChecked = false

            eachNDaysText.isEnabled = false
            treatmentPeriodCycle.isEnabled = true
            pausePeriodCycle.isEnabled = true
            setValueToCheckboxes(false)
        }
    }

    private fun setValueToCheckboxes(value: Boolean) {
        mondayBox.isEnabled = value
        tuesdayBox.isEnabled = value
        wednesdayBox.isEnabled = value
        thursdayBox.isEnabled = value
        fridayBox.isEnabled = value
        saturdayBox.isEnabled = value
        sundayBox.isEnabled = value
    }

    @SuppressLint("SimpleDateFormat")
    private fun handleNextButtonClick() {
        val chosenKit = kitSpinner.selectedItem as FirstAidKit

        if (chosenKit.getName() == "Не выбрано") {
            if (medicineName.text == null || medicineName.text.toString().trim() == "") {
                medicineName.error = "Введите название препарата "
                return
            }

            if (units.text == null || units.text.toString().trim() == "") {
                units.error = "Введите единицы измерения "
                return
            }
        }

        var treatment: Treatment? = null
        if (everyDayRadio.isChecked) {
            treatment = Treatment()
            treatment.setConsumptionType(ConsumptionType.EVERY_DAY)
        } else if (eachNDaysRadio.isChecked) {
            if (eachNDaysText.text == null || eachNDaysText.text.toString().trim() == "") {
                eachNDaysText.error = "Введите число "
                return
            }

            treatment = Treatment()
            treatment.setConsumptionType(ConsumptionType.EACH_N_DAYS)
            treatment.setEachNDays(eachNDaysText.text.toString().toInt())
        } else if (daysOfWeekRadio.isChecked) {
            var daysOfWeek = 0
            for (i in 0..6) {
                if (daysArray[i].isChecked) {
                    daysOfWeek += pow(2, i)
                }
            }

            if (daysOfWeek == 0) {
                daysOfWeekRadio.error = "Выберите дни недели "
                return
            }

            treatment = Treatment()
            treatment.setConsumptionType(ConsumptionType.DAYS_OF_WEEK)
            treatment.setDaysOfWeek(daysOfWeek)
        } else if (cyclicRadio.isChecked) {
            if (treatmentPeriodCycle.text == null || treatmentPeriodCycle.text.toString()
                    .trim() == ""
            ) {
                treatmentPeriodCycle.error = "Введите срок приёма "
                return
            }

            if (pausePeriodCycle.text == null || pausePeriodCycle.text.toString().trim() == "") {
                pausePeriodCycle.error = "Введите срок паузы "
            }

            treatment = Treatment()
            treatment.setConsumptionType(ConsumptionType.CYCLIC)
            treatment.setConsumptionPeriod(treatmentPeriodCycle.text.toString().toInt())
            treatment.setHaltPeriod(pausePeriodCycle.text.toString().toInt())
        }

        if (medicineNameSpinner.isVisible) {
            var chosenMed = medicineNameSpinner.selectedItem as Medicine

            if (chosenMed.getName().equals("Не выбрано")) {
                Toast.makeText(activity as TreatmentActivity, "Выберите препарат", Toast.LENGTH_SHORT).show()
                return
            }
            treatment!!.setMedicineId(chosenMed.getId())
            treatment.setMedicineName(chosenMed.getName())
        } else {
            treatment!!.setMedicineName(medicineName.text.toString())
            treatment.setUnit(units.text.toString())
        }

        if (lengthText.text.toString().trim() == "" || lengthText.text.toString().toIntOrNull() == null) {
            lengthText.error = "Введите продолжительность приёма"
            return
        }

        treatment.setLength(lengthText.text.toString().toInt())

        treatment.setInstruction(instructionText.text.toString())
        treatment.setStartDate(
            SimpleDateFormat("yyyy.MM.dd").format(
                SimpleDateFormat("dd.MM.yyyy").parse(
                    startDate.text.toString()
                )!!
            )
        )
        (activity as TreatmentActivity).addTreatment(treatment)
    }
}