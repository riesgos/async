package org.n.riesgos.asyncwrapper.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "wps")
data class WPSConfiguration(val wpsURL: String, val process: String, val wpsVersion : String, val outputs : List<WPSOutputDefinition>)
