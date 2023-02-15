package org.n.riesgos.asyncwrapper.config

data class RetryConfiguration(var attempts: Int, var backoff_millis: Long)