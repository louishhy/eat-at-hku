package hk.hkucs.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.Volley.newRequestQueue
import hk.hkucs.demo.global.IP
import org.json.JSONArray
import org.json.JSONObject

@Suppress("RedundantSamConstructor")
class InfoActivity: AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val infoResponse = findViewById<TextView>(R.id.infoResponse)
        val infoTestButton = findViewById<Button>(R.id.infoTest)
        val infoBackButton = findViewById<Button>(R.id.infoBack)
        val infoText = findViewById<TextView>(R.id.infoText)

        infoText.text = "Eat @ HKU Application demo version\nAndroid Developer: Zhu Wendi\nBackend Developer: Huang Haoyu\n" +
                        ""

        infoTestButton.setOnClickListener() {

            val url = IP.ipAddress + "test"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    infoResponse.text = response.get("result").toString()
                    //Log.w("myTag", "2")
                },
                Response.ErrorListener { error ->
                    infoResponse.text = error.toString()
                    //Log.w("myTag", error.toString())
                }
            )
            newRequestQueue(this).add(jsonObjectRequest)
        }

        infoBackButton.setOnClickListener() {

            val intent = Intent(this@InfoActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.left_enter,R.anim.left_exit)

        }


    }
}