package org.n.riesgos.asyncwrapper.config


data class RetryConfiguration(val maxRetries: Int = 3, val backoffMillis: Long = 3000){
    init {
        require(maxRetries >= 0) { "maxRetries must be greater than or equal to zero" }
        require(backoffMillis >= 0) {"backoffMillis must be greater than or equal to zero"}
    }
}
