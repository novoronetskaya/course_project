package com.course_project.voronetskaya.view.fragments.kit_activity

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Medicine
import com.course_project.voronetskaya.view.activities.KitActivity
import com.course_project.voronetskaya.barcode.BarcodeApiCaller
import com.course_project.voronetskaya.barcode.Product
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.*

class NewMedicineFragment : Fragment() {
    private lateinit var kitName: TextView
    private lateinit var medicineName: EditText
    private lateinit var expirationDate: EditText
    private lateinit var producer: EditText
    private lateinit var amount: EditText
    private lateinit var pharmEffect: EditText
    private lateinit var symptom: EditText
    private lateinit var consumptionType: EditText
    private lateinit var unit: EditText
    private lateinit var remains: EditText

    private lateinit var codeBar: ImageView
    private lateinit var cancelButton: Button
    private lateinit var readyButton: Button

    private var formattedDate = ""

    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_medicine_fragment, container, false)

        kitName = view.findViewById(R.id.tv_kit_name)
        medicineName = view.findViewById(R.id.et_medicine_name)
        expirationDate = view.findViewById(R.id.et_medicine_expiration_date)
        producer = view.findViewById(R.id.et_medicine_producer)
        amount = view.findViewById(R.id.et_medicine_amount)
        pharmEffect = view.findViewById(R.id.et_pharm_effect)
        symptom = view.findViewById(R.id.et_symptom)
        consumptionType = view.findViewById(R.id.et_consumption_type)
        unit = view.findViewById(R.id.et_unit)
        remains = view.findViewById(R.id.et_remainders)

        codeBar = view.findViewById(R.id.iv_codebar)
        cancelButton = view.findViewById(R.id.b_cancel)
        readyButton = view.findViewById(R.id.b_ready)

        kitName.text = requireArguments().getString("kitName")
        codeBar.bringToFront()
        codeBar.setOnClickListener {
            if (activity?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 1)
            } else {
                activity?.requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            }
        }

        readyButton.setOnClickListener {
            handleReadyButtonClick()
        }

        cancelButton.setOnClickListener {
            (activity as KitActivity).popStack()
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
            dialog.datePicker.minDate = calendar.timeInMillis
            dialog.show()
        }
        return view
    }

    private fun handleReadyButtonClick() {
        if (medicineName.text == null || medicineName.text.toString().trim() == "") {
            medicineName.error = "Введите название препарата "
            return
        }

        if (expirationDate.text == null || expirationDate.text.toString().trim() == "") {
            expirationDate.error = "Введите срок годности "
            return
        }

        if (producer.text == null || producer.text.toString().trim() == "") {
            producer.error = "Введите производителя "
            return
        }

        if (amount.text == null || amount.text.toString().toDoubleOrNull() == null) {
            amount.error = "Введите количество "
            return
        }

        if (pharmEffect.text == null || pharmEffect.text.toString().trim() == "") {
            pharmEffect.error = "Введите фармаоклогический эффект "
            return
        }

        if (symptom.text == null || symptom.text.toString().trim() == "") {
            symptom.error = "Введите симптом "
            return
        }

        if (consumptionType.text == null || consumptionType.text.toString().trim() == "") {
            consumptionType.error = "Введите форму приёма "
            return
        }

        if (unit.text == null || unit.text.toString().trim() == "") {
            unit.error = "Введите единицы измерения "
            return
        }

        if (remains.text == null || remains.text.toString().toDoubleOrNull() == null) {
            remains.error = "Введите остаток "
            return
        }

        val medicine = Medicine()
        medicine.setName(medicineName.text.toString())
        medicine.setExpirationDate(formattedDate)
        medicine.setProducer(producer.text.toString())
        medicine.setPharmEffect(pharmEffect.text.toString())
        medicine.setSymptom(symptom.text.toString())
        medicine.setAmount(amount.text.toString().trim().toDouble())
        medicine.setConsumptionForm(consumptionType.text.toString())
        medicine.setUnit(unit.text.toString())
        medicine.setNotificationRemains(remains.text.toString().trim().toDouble())

        (activity as KitActivity).addMedicine(medicine)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val picture = (data?.extras?.get("data")) as Bitmap
            val options =
                BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_EAN_13).build()
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(InputImage.fromBitmap(picture, 0))
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isEmpty()) {
                        Toast.makeText(activity, "Штрих-код не найден", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    BarcodeRequest(activity as KitActivity, barcodes[0].rawValue!!).execute()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    inner class BarcodeRequest(private var activity: KitActivity, private var barcode: String) :
        AsyncTask<Unit, Unit, Product?>() {
        override fun onPreExecute() {

        }

        override fun doInBackground(vararg p0: Unit?): Product? {
            return try {
                BarcodeApiCaller().getDataByCode(barcode)
            } catch (e: java.lang.Exception) {
                null
            }
        }

        override fun onPostExecute(result: Product?) {
            if (result == null) {
                Toast.makeText(activity, "Препарат не найден", Toast.LENGTH_SHORT).show()
            } else {
                if (result.manufacturer?.titles?.containsKey("ru") == true) {
                    producer.setText(result.manufacturer.titles["ru"])
                }

                if (result.titles?.containsKey("ru") == true) {
                    medicineName.setText(result.titles["ru"])
                }
            }
        }
    }
}
