package org.n.riesgos.asyncwrapper.process

interface Process {
    fun runProcess(input: ProcessInput) : ProcessOutput
}