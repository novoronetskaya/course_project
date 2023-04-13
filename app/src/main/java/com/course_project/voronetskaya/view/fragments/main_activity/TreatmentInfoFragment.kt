package com.course_project.voronetskaya.view.fragments.main_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.util.ConsumptionType
import com.course_project.voronetskaya.data.util.Status
import com.course_project.voronetskaya.view.activities.MainActivity
import com.course_project.voronetskaya.view.model.TreatmentHistoryAndTreatment
import java.text.SimpleDateFormat
import java.util.*

class TreatmentInfoFragment : Fragment() {
    private lateinit var timeText: TextView
    private lateinit var doseText: TextView
    private lateinit var consumptionText: TextView
    private lateinit var instructionText: TextView
    private lateinit var medicineText: TextView

    private lateinit var take: Button
    private lateinit var postpone: Button
    private lateinit var miss: Button

    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.treatment_info_fragment, container, false)
        timeText = view.findViewById(R.id.tv_time)
        doseText = view.findViewById(R.id.tv_dose)
        consumptionText = view.findViewById(R.id.tv_consumption_form)
        instructionText = view.findViewById(R.id.tv_instruction)
        medicineText = view.findViewById(R.id.tv_medicine_name)

        take = view.findViewById(R.id.b_take)
        postpone = view.findViewById(R.id.b_postpone)
        miss = view.findViewById(R.id.b_miss)

        val treatment = (activity as MainActivity).getViewModel().getTreatmentWithTime()
        if (treatment !is TreatmentHistoryAndTreatment) {
            take.visibility = View.GONE
            postpone.visibility = View.GONE
            miss.visibility = View.GONE
        }

        timeText.text = String.format("Время приёма: %s", treatment.time)
        doseText.text = String.format("Доза: %.02f", treatment.dose)

        when (treatment.treatment.getConsumptionType()) {
            ConsumptionType.EVERY_DAY -> consumptionText.text = "Тип приёма: каждый день"
            ConsumptionType.EACH_N_DAYS -> consumptionText.text =
                String.format("Тип приёма: раз в %d дней", treatment.treatment.getEachNDays())
            ConsumptionType.DAYS_OF_WEEK -> consumptionText.text =
                "Тип приёма: в определённые дни недели"
            ConsumptionType.CYCLIC -> consumptionText.text = String.format(
                "Тип приёма: циклический (%d дней приёма, %d дней перерыва",
                treatment.treatment.getConsumptionPeriod(),
                treatment.treatment.getHaltPeriod()
            )
        }

        instructionText.text =
            String.format("Инструкция по приёму: %s", treatment.treatment.getInstruction())
        medicineText.text = treatment.treatment.getMedicineName()

        take.setOnClickListener {
            if (treatment is TreatmentHistoryAndTreatment) {
                (activity as MainActivity).getViewModel()
                    .updateHistoryStatus(
                        treatment.treatmentHistory,
                        Status.TAKEN,
                        dateFormatter.format(Date())
                    )


                (activity as MainActivity).getViewModel().reduceMedicine(
                    treatment.treatment.getMedicineId(),
                    treatment.treatmentHistory.getDose()
                )
                if ((activity as MainActivity).getViewModel().notEnoughMedicine()) {
                    sendNotification(treatment)
                }


            }
            (activity as MainActivity).popStack()
        }

        postpone.setOnClickListener {
            if (treatment is TreatmentHistoryAndTreatment) {
                (activity as MainActivity).getViewModel()
                    .updateHistoryStatus(
                        treatment.treatmentHistory,
                        Status.MOVED,
                        dateFormatter.format(Date())
                    )

                val time =
                    dateFormatter.parse(treatment.treatmentHistory.getMovedTo())!!.time + 1000 * 60 * 30
                (activity as MainActivity).getViewModel()
                    .updateHistoryTime(treatment.treatmentHistory, dateFormatter.format(Date(time)))
            }
            (activity as MainActivity).popStack()
        }

        miss.setOnClickListener {
            if (treatment is TreatmentHistoryAndTreatment) {
                (activity as MainActivity).getViewModel()
                    .updateHistoryStatus(treatment.treatmentHistory, Status.CANCELLED)
            }
            (activity as MainActivity).popStack()
        }
        return view
    }

    private fun sendNotification(treatment: TreatmentHistoryAndTreatment) {
        val notification = NotificationCompat.Builder(activity as MainActivity, "1")
            .setContentText(
                String.format(
                    "Препарат %s заканчивается! ",
                    treatment.treatment.getMedicineName()
                )
            )
            .setSmallIcon(R.drawable.auth_screen_icon)
            .setContentTitle("Органайзер лекарств")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
        val manager = NotificationManagerCompat.from(activity as MainActivity)
        manager.notify(-1, notification.build())
    }
}