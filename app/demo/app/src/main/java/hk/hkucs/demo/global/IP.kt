package hk.hkucs.demo.global

import android.app.Application

public class IP : Application() {
    companion object {
        @JvmField
        var ipAddress: String = "http://10.68.104.199:8081/"
    }
}