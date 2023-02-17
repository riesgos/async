package org.n.riesgos.asyncwrapper.config


data class RetryConfiguration(val maxRetries: Int = 3, val backoffMillis: Long = 3000)
