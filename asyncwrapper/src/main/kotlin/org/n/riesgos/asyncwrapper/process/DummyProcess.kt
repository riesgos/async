package org.n.riesgos.asyncwrapper.process

import org.n.riesgos.asyncwrapper.process.Process

class DummyProcess : Process {

    override fun runProcess(input: ProcessInput): ProcessOutput {
        println("dummy process: $input")
        return ProcessOutput("dummyProcess", HashMap<String, InlineParameter>(), HashMap<String, List<ReferenceParameter>>() );
    }
}