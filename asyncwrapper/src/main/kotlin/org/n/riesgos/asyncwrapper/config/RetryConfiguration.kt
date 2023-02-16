package org.n.riesgos.asyncwrapper.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


data class RetryConfiguration(var attempts: Int = 3, var backoff_millis: Long = 3000)
