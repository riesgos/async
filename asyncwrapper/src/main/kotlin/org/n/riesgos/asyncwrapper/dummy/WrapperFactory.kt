package org.n.riesgos.asyncwrapper.dummy

import org.n.riesgos.asyncwrapper.config.AppConfiguration
import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n.riesgos.asyncwrapper.datamanagement.DatamanagementRepo
import org.n.riesgos.asyncwrapper.pulsar.PulsarPublisher
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class WrapperFactory(val datamgmtRepo : DatamanagementRepo, val appConfig: AppConfiguration, val wpsConfig: WPSConfiguration, val pulsarPublisher: PulsarPublisher) {

    fun createWrapper() : AbstractWrapper{
        val fullClassName = appConfig.wrapperClass
        val classDef = Class.forName(fullClassName)
        val cons = classDef.getConstructor(DatamanagementRepo::class.java, WPSConfiguration::class.java, PulsarPublisher::class.java)
        val wrapperInst = cons.newInstance(datamgmtRepo, wpsConfig, pulsarPublisher) as AbstractWrapper

        println("init wrapper: " + wrapperInst.javaClass.name)

        return wrapperInst;
    }
}