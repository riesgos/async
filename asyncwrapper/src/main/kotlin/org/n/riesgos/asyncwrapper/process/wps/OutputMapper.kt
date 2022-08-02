package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.datamanagement.models.ComplexOutput
import org.n.riesgos.asyncwrapper.process.ProcessInput
import org.n.riesgos.asyncwrapper.process.ProcessOutput
import java.net.PortUnreachableException
import kotlin.random.Random

abstract class OutputMapper(private val jobId : Long,  private val output : ProcessOutput) {

    fun mapInputs() : List<ComplexOutput>{
        val complexOutputs = ArrayList<ComplexOutput>()

        for (refParams in output.referenceParameters){ //only complex reference outputs
            for(refParam in refParams.value) {
                val compOut = ComplexOutput(Random.nextLong(), jobId, refParams.key, refParam.link, refParam.mimeType, refParam.schema, refParam.encoding)
                complexOutputs.add(compOut)
            }
        }

        return complexOutputs;
    }

}