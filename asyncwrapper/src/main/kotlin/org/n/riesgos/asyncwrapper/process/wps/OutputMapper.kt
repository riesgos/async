package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.process.ProcessInput
import org.n.riesgos.asyncwrapper.process.ProcessOutput
import java.net.PortUnreachableException
import kotlin.random.Random

class OutputMapper(private val jobId : Long,  private val output : ProcessOutput) {

    fun mapOutputs() : List<ComplexOutput>{
        val complexOutputs = ArrayList<ComplexOutput>()

        for (refParams in output.referenceParameters){ //only complex reference outputs



            // not sure if that works
            // TODO: Recheck
            /*for(refParam in refParams.value) {
                val compOut = ComplexOutput(null, jobId, refParams.key, refParam.link, refParam.mimeType, refParam.schema, refParam.encoding) //id is set by database
                complexOutputs.add(compOut)
            }*/

            // But for the moment do something like this
            complexOutputs.add(ComplexOutput(null, jobId, refParams.id, refParams.reference.href.toString(),
                    refParams.format.mimeType,
                    refParams.format.schema,
                    refParams.format.encoding
            ))
        }

        return complexOutputs;
    }

}