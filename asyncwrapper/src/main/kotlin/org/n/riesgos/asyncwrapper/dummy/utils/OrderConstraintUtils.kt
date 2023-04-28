package org.n.riesgos.asyncwrapper.dummy.utils

import org.json.JSONObject
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.datamanagement.models.*
import org.n.riesgos.asyncwrapper.datamanagement.utils.getStringOrDefault
import java.util.*

class OrderConstraintUtils (val datamanagementRepo: DatamanagementRepo) {
    fun getOrderConstraints (orderId: Long, wrapperName: String, loggerInfo: (x: String) -> Unit): ParsedConstraintsResult? {
        val jsonObject = datamanagementRepo.orderConstraints(orderId)
        if (jsonObject == null) {
            return null
        }
        if (!jsonObject.has(wrapperName)) {
            return OrderConstraintsResult(HashMap(), HashMap(), HashMap())
        }
        val wrapperRawConstraints = jsonObject.getJSONObject(wrapperName)

        if (wrapperRawConstraints.has("job_id")) {
            val jobId = wrapperRawConstraints.getLong("job_id")
            return JobIdConstraintResult(jobId)
        }

        val literalConstraints = HashMap<String, MutableList<String>>()
        val complexConstraints = HashMap<String, MutableList<ComplexInputConstraint>>()
        val bboxConstraints = HashMap<String, MutableList<BBoxInputConstraint>>()

        if (wrapperRawConstraints.has("literal_inputs")) {
            val literalInputConstraints = wrapperRawConstraints.getJSONObject("literal_inputs")
            for (key in literalInputConstraints.keySet()) {
                val constraintArray = literalInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    val existingList = literalConstraints.getOrDefault(key, ArrayList())
                    existingList.add(constraintValue as String)
                    literalConstraints.put(key, existingList)
                    loggerInfo("Added literal input constraint for " + wrapperName + " " + key + ": " + constraintValue.toString())
                }
            }
        }
        if (wrapperRawConstraints.has("complex_inputs")) {
            val complexInputConstraints = wrapperRawConstraints.getJSONObject("complex_inputs")
            for (key in complexInputConstraints.keySet()) {
                val constraintArray = complexInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    val constraintObject = constraintValue as JSONObject
                    val existingList = complexConstraints.getOrDefault(key, ArrayList())
                    existingList.add(
                            ComplexInputConstraint(
                                    constraintObject.getStringOrDefault("link", null),
                                    constraintObject.getStringOrDefault("input_value", null),
                                    constraintObject.getString("mime_type"),
                                    constraintObject.getString("xmlschema"),
                                    constraintObject.getString("encoding")
                            )
                    )
                    complexConstraints.put(key, existingList)
                    loggerInfo("Added complex input constraint for " + wrapperName + " " + key)
                }
            }
        }
        if (wrapperRawConstraints.has("bbox_inputs")) {
            val bboxInputConstraints = wrapperRawConstraints.getJSONObject("bbox_inputs")
            for (key in bboxInputConstraints.keySet()) {
                val constraintArray = bboxInputConstraints.getJSONArray(key)
                for (constraintValue in constraintArray) {
                    val constraintObject = constraintValue as JSONObject
                    val existingList = bboxConstraints.getOrDefault(key, ArrayList())
                    existingList.add(
                            BBoxInputConstraint(
                                    constraintObject.getDouble("lower_corner_x"),
                                    constraintObject.getDouble("lower_corner_y"),
                                    constraintObject.getDouble("upper_corner_x"),
                                    constraintObject.getDouble("upper_corner_y"),
                                    constraintObject.getString("crs")
                            )
                    )
                    bboxConstraints.put(key, existingList)
                    loggerInfo("Added bbox input constraint for " + wrapperName + " " + key)
                }
            }
        }

        return OrderConstraintsResult(literalConstraints, complexConstraints, bboxConstraints)
    }
}