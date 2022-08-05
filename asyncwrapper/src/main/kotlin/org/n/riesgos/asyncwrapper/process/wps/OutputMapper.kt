package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.process.ProcessInput
import org.n.riesgos.asyncwrapper.process.ProcessOutput
import java.net.PortUnreachableException
import java.util.*

class OutputMapper(private val jobId : Long,  private val output : ProcessOutput) {

    fun mapOutputs() : List<ComplexOutput>{
        val complexOutputs = ArrayList<ComplexOutput>()

        for (key in output.referenceParameters.keys) {
            val referenceParameters = output.referenceParameters.get(key)!!
            for (refParams in referenceParameters) {
                // not sure if that works
                // TODO: Recheck
                /*for(refParam in refParams.value) {
                    val compOut = ComplexOutput(null, jobId, refParams.key, refParam.link, refParam.mimeType, refParam.schema, refParam.encoding) //id is set by database
                    complexOutputs.add(compOut)
                }*/
                val wpsIdentifier = key
                val link = refParams.link
                val mimeType = refParams.mimeType
                val xmlschema = refParams.schema
                val encoding = refParams.encoding

                // But for the moment do something like this
                complexOutputs.add(ComplexOutput(
                        null,
                        jobId,
                        wpsIdentifier,
                        link,
                        mimeType,
                        xmlschema,
                        encoding
                ))
            }
        }

        return complexOutputs;
    }

}