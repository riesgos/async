package org.n.riesgos.asyncwrapper.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties
@ConstructorBinding
data class AppConfiguration(val appID: String)