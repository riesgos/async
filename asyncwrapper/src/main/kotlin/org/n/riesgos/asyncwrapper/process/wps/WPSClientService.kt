package org.n.riesgos.asyncwrapper.process.wps

import org.n.riesgos.asyncwrapper.config.WPSConfiguration
import org.n52.geoprocessing.wps.client.WPSClientException
import org.n52.geoprocessing.wps.client.WPSClientSession
import org.springframework.stereotype.Service

@Service
class WPSClientService(val config : WPSConfiguration) {

    @Synchronized
    fun establishWPSConnection() : WPSClientSession{
        //val wpsClient = WPSClientSession.getInstance()
        val wpsClient = WPSClientSession()
        //connect session
        var connected = true;
        if(!wpsClient.serviceAlreadyRegistered(config.wpsURL)) {
            connected = wpsClient.connect(config.wpsURL, config.wpsVersion)
        }

        if(connected){
            return wpsClient
        }else{
            throw WPSClientException("unable to connect to wps at ${config.wpsURL}")
        }
    }

}