package hk.hkucs.demo.global

import android.app.Application

public class IP : Application() {
    companion object {
        @JvmField
        var ipAddress: String = "http://172.20.10.5:8081/"
    }
}