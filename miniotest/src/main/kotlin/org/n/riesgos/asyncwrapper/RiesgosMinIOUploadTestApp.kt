package org.n.riesgos.asyncwrapper

import org.n.riesgos.asyncwrapper.config.FilestorageConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
		FilestorageConfig::class
)
class RiesgosMinIOUploadTestApp

fun main(args: Array<String>) {
	runApplication<RiesgosMinIOUploadTestApp>(*args)
}
