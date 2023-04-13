package com.course_project.voronetskaya.view.model

import com.course_project.voronetskaya.data.model.Treatment
import com.course_project.voronetskaya.data.model.TreatmentHistory

class TreatmentHistoryAndTreatment(
    override val treatment: Treatment,
    val treatmentHistory: TreatmentHistory
) : TreatmentWithTime(treatment, treatmentHistory.getMovedTo().split(' ')[1], treatmentHistory.getDose())