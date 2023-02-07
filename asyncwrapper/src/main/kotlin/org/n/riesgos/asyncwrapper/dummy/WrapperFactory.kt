package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.AppConfiguration
import org.n.riesgos.asyncwrapper.config.FilestorageConfig
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class WrapperFactory(val datamgmtRepo : DatamanagementRepo, val appConfig: AppConfiguration, val wpsConfig: WPSConfiguration, val pulsarPublisher: PulsarPublisher, val filestorageConfig: FilestorageConfig) {

    companion object {
        val LOGGER = Logger.getLogger("WrapperFactory")
    }

    fun createWrapper() : AbstractWrapper{
        val fullClassName = appConfig.wrapperClass
        val classDef = Class.forName(fullClassName)
        val cons = classDef.getConstructor(DatamanagementRepo::class.java, WPSConfiguration::class.java, PulsarPublisher::class.java, FilestorageConfig::class.java)
        val wrapperInst = cons.newInstance(datamgmtRepo, wpsConfig, pulsarPublisher, filestorageConfig) as AbstractWrapper

        LOGGER.info("init wrapper: " + wrapperInst.javaClass.name)

        return wrapperInst;
    }
}