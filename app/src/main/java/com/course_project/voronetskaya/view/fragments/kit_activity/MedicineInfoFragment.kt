package com.course_project.voronetskaya.view.fragments.kit_activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Medicine
import com.course_project.voronetskaya.view.activities.KitActivity
import java.util.*

class MedicineInfoFragment : Fragment() {
    private lateinit var addButton: Button
    private lateinit var saveButton: Button
    private lateinit var refillsButton: Button

    private lateinit var kitName: TextView
    private lateinit var medicineName: TextView
    private lateinit var expirationDate: EditText
    private lateinit var producer: EditText
    private lateinit var remains: TextView
    private lateinit var pharmEffect: EditText
    private lateinit var symptom: EditText

    private var formattedDate = ""
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.medicine_info_fragment, container, false)

        addButton = view.findViewById(R.id.b_refill)
        saveButton = view.findViewById(R.id.b_save)
        refillsButton = view.findViewById(R.id.b_refills)
        kitName = view.findViewById(R.id.tv_kit_name)
        medicineName = view.findViewById(R.id.tv_medicine_name)
        expirationDate = view.findViewById(R.id.et_medicine_expiration_date)
        producer = view.findViewById(R.id.et_medicine_producer)
        remains = view.findViewById(R.id.tv_medicine_remainders)
        pharmEffect = view.findViewById(R.id.et_medicine_pharm_effect)
        symptom = view.findViewById(R.id.et_medicine_symptom)

        kitName.text = requireArguments().getString("kitName")

        val medicine = (activity as KitActivity).getViewModel().getMedicine()
        medicine.observe(viewLifecycleOwner) { med ->
            setData(med)
        }

        addButton.setOnClickListener {
            val editText = EditText(activity)
            editText.textSize = 21F
            editText.setText("0")
            editText.inputType = InputType.TYPE_CLASS_NUMBER

            AlertDialog.Builder(activity).setTitle("Пополнение препарата ")
                .setMessage(
                    String.format(
                        "На сколько единиц (%s) Вы хотите пополнить? ",
                        medicine.value!!.getUnit()
                    )
                ).setView(editText).setPositiveButton("Готово") { _, _ ->
                    if (editText.text == null || editText.text.toString().trim() == "") {
                        editText.error = "Введите количество"
                        return@setPositiveButton
                    }

                    (activity as KitActivity).addRefill(
                        medicine.value!!,
                        editText.text.toString().toInt()
                    )
                }.setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        saveButton.setOnClickListener {
            if (expirationDate.text == null || expirationDate.text.toString().trim() == "") {
                expirationDate.error = "Введите срок годности "
                return@setOnClickListener
            }

            if (producer.text == null || producer.text.toString().trim() == "") {
                producer.error = "Введите производителя "
                return@setOnClickListener
            }

            if (pharmEffect.text == null || pharmEffect.text.toString().trim() == "") {
                pharmEffect.error = "Введите фармакологическое действие "
                return@setOnClickListener
            }

            if (symptom.text == null || symptom.text.toString().trim() == "") {
                symptom.error = "Введите симптом "
                return@setOnClickListener
            }

            medicine.value!!.setExpirationDate(formattedDate)
            medicine.value!!.setProducer(producer.text.toString())
            medicine.value!!.setPharmEffect(pharmEffect.text.toString())
            medicine.value!!.setSymptom(symptom.text.toString())
            (activity as KitActivity).updateMedicine(medicine.value!!)
        }

        refillsButton.setOnClickListener {
            (activity as KitActivity).showRefillsHistory(medicine.value!!.getId(), medicine.value!!.getName())
        }

        calendar = Calendar.getInstance()
        expirationDate.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as KitActivity,
                { _, year, month, day ->
                    expirationDate.setText(String.format("%02d.%02d.%d", day, month + 1, year))
                    formattedDate = String.format("%d.%02d.%02d", year, month + 1, day)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }
        return view
    }

    private fun setData(medicine: Medicine) {
        medicineName.text = medicine.getName()

        val date = medicine.getExpirationDate().split('.')
        expirationDate.setText(String.format("%s.%s.%s", date[2], date[1], date[0]))
        producer.setText(medicine.getProducer())
        pharmEffect.setText(medicine.getPharmEffect())
        remains.text = String.format("Остаток: %.02f %s", medicine.getAmount(), medicine.getUnit())
        symptom.setText(medicine.getSymptom())
    }
}