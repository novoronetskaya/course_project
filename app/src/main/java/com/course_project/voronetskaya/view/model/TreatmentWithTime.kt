package com.course_project.voronetskaya.view.model

import com.course_project.voronetskaya.data.model.Treatment

open class TreatmentWithTime (open val treatment: Treatment, val time: String, val dose: Double)