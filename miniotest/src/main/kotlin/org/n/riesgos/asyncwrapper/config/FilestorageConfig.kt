package org.n.riesgos.asyncwrapper.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "filestorage")
@ConstructorBinding
data class FilestorageConfig(val endpoint: String, val user: String, val password: String, val bucketName: String, val access: String)