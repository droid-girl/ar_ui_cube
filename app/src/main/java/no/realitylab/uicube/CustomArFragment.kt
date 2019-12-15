package no.realitylab.uicube

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment : ArFragment() {
    override fun getSessionConfiguration(session: Session?): Config {
        val config = Config(session)
        config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        return config
    }
}