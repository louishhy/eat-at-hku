package hk.hkucs.demo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import hk.hkucs.demo.global.IP

@Suppress("RedundantSamConstructor")
class ImageActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val r_id = intent.getStringExtra("r_id")
        val extraDataRestaurantName = intent.getStringExtra("restaurant")
        val username = intent.getStringExtra("username")
        val extraDataRestaurantAddress = intent.getStringExtra("address")

        val backButton = findViewById<Button>(R.id.imagePageBackButton)
        val title = findViewById<TextView>(R.id.imagePageTitle)
        //val display = findViewById<ImageView>(R.id.imagePageImage)

        title.text = extraDataRestaurantName

        backButton.setOnClickListener() {
            val intent = Intent(this@ImageActivity, RestaurantActivity::class.java)
            intent.putExtra("r_id",r_id)
            intent.putExtra("username",username)
            intent.putExtra("name",extraDataRestaurantName)
            intent.putExtra("address",extraDataRestaurantAddress)
            startActivity(intent)
            overridePendingTransition(R.anim.right_enter,R.anim.right_exit)
        }

        httpGetImage(r_id!!)

    }

    private fun httpGetImage(ID: String) {
        val image = findViewById<ImageView>(R.id.imagePageImage)
        val url = IP.ipAddress + "canteens/menuImage?canteenID=" + ID
        //val url = "http://10.68.104.199:8081/canteens/canteenLargeImage?canteenID=" + ID
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val ret = response.get("url").toString()
                Log.w("myTagImage", "http://10.68.104.199:8081/" + ret)
                DownloadImageFromInternet(image).execute("http://10.68.104.199:8081/" + ret)
                Log.w("myTagImage", response.toString())
            }, Response.ErrorListener { error ->
                Log.w("myTagImage", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
            Toast.makeText(applicationContext, "Loading Image Now",     Toast.LENGTH_SHORT).show()
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

}