package hk.hkucs.demo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import hk.hkucs.demo.global.IP
import hk.hkucs.demo.model.CommentData
import hk.hkucs.demo.model.RestaurantData
import hk.hkucs.demo.view.CommentAdapter
import hk.hkucs.demo.view.RestaurantAdapter
import org.json.JSONObject
import per.wsj.library.AndRatingBar
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@Suppress("RedundantSamConstructor")
class RestaurantActivity : AppCompatActivity() {

    private lateinit var recv: RecyclerView
    private lateinit var commentList:ArrayList<CommentData>
    private lateinit var commentAdapter: CommentAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        val r_id = intent.getStringExtra("r_id")
        val username = intent.getStringExtra("username")
        val extraDataRestaurantName = intent.getStringExtra("name")
        val extraDataRestaurantAddress = intent.getStringExtra("address")
        val pageRating = findViewById<AndRatingBar>(R.id.restaurantPageRating)
        val pageCongestion = findViewById<AndRatingBar>(R.id.restaurantPageCongestion)
        val pageTitle = findViewById<TextView>(R.id.restaurantPageTitle)
        val backToPageButton = findViewById<Button>(R.id.backToPageButton)
        val postCommentButton = findViewById<Button>(R.id.postCommentButton)

        val pageDescription = findViewById<TextView>(R.id.restaurantPageDescription)
        val update = findViewById<Button>(R.id.restaurantPageUpdateButton)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.commentSwipeRefresh)
        val cardInfo = findViewById<CardView>(R.id.restaurantPageCardInfo)


        pageTitle.text = extraDataRestaurantName
        pageDescription.text = "Name: ".plus(extraDataRestaurantName.plus("\nAddress: ".plus(extraDataRestaurantAddress.plus("\nClick to view the image!"))))
        httpRefreshPage(r_id!!)
        httpGetImage(r_id!!)


        backToPageButton.setOnClickListener() {
            val intent = Intent(this@RestaurantActivity, PageActivity::class.java)
            intent.putExtra("username",username)
            startActivity(intent)
            overridePendingTransition(R.anim.right_enter,R.anim.right_exit)
        }
        cardInfo.setOnClickListener() {
            val intent = Intent(this@RestaurantActivity, ImageActivity::class.java)
            intent.putExtra("r_id",r_id)
            intent.putExtra("username",username)
            intent.putExtra("restaurant",extraDataRestaurantName)
            intent.putExtra("address",extraDataRestaurantAddress)
            startActivity(intent)
            overridePendingTransition(R.anim.left_enter,R.anim.left_exit)
        }
        update.setOnClickListener() {
            val v = LayoutInflater.from(this).inflate(R.layout.update_item,null)
            val which = v.findViewById<TextView>(R.id.updateWhichRestaurant)
            val bar = v.findViewById<RatingBar>(R.id.congestionBar)
            val addDialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

            which.text = extraDataRestaurantName

            addDialog.setView(v)
            addDialog.setPositiveButton("Upload"){ dialog,_->
                if (bar.rating >= 0.5f) {
                    //update action
                    httpUpdateRestaurantCongestion(r_id!!, bar.rating)
                    dialog.dismiss()
                    httpRefreshPage(r_id!!)
                    //httpGetImage(r_id!!)
                }
            }
            addDialog.create()
            addDialog.show()

        }

        httpRefreshComment(r_id!!)

        swipeRefresh.setOnRefreshListener () {
            httpRefreshPage(r_id!!)
            //httpGetImage(r_id!!)
            httpRefreshComment(r_id!!)
        }

        postCommentButton.setOnClickListener() {
            val p = LayoutInflater.from(this).inflate(R.layout.postcomment_item,null)
            val bar = p.findViewById<RatingBar>(R.id.ratingBar)
            val context = p.findViewById<EditText>(R.id.postCommentContext)
            val postDialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

            postDialog.setView(p)
            postDialog.setPositiveButton("Upload"){ dialog,_->
                if (bar.rating >= 0.5f && context.text.toString().length > 0) {
                    //post action
                    Log.w("myTag11", "1")
                    httpPostComment(r_id!!, username!!, context.text.toString(), bar.rating)
                    Log.w("myTag11", "2")
                    dialog.dismiss()
                    httpRefreshPage(r_id!!)
                    httpRefreshComment(r_id!!)
                }
            }
            postDialog.create()
            postDialog.show()
        }

    }
    private fun httpGetImage(ID: String) {
        val image = findViewById<ImageView>(R.id.restaurantPageImage)
        val url = IP.ipAddress + "canteens/canteenLargeImage?canteenID=" + ID
        //val url = "http://10.68.104.199:8081/canteens/canteenLargeImage?canteenID=" + ID
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val ret = response.get("url").toString()
                //Log.w("myTagImage", "http://10.68.104.199:8081/" + ret)
                DownloadImageFromInternet(image).execute(IP.ipAddress + ret)
                Log.w("myTagImage", response.toString())
            }, Response.ErrorListener { error ->
                Log.w("myTagImage", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)

    }

    private fun httpUpdateRestaurantCongestion(ID: String, rating: Float) {
        class Data(
            @Json(index = 1) val canteenID: String,
            @Json(index = 2) val userID: String,
            @Json(index = 3) val congestionRanking: Double
        )
        val url = IP.ipAddress + "canteens/postcongestion"
        //val url = "http://10.68.104.199:8081/canteens/postcongestion"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, JSONObject(Klaxon().toJsonString(Data(ID, "NULL", rating.toDouble()))),
            Response.Listener { response ->
                Log.w("myTag", response.toString())
            }, Response.ErrorListener { error ->
                Log.w("myTag", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)

    }

    private fun httpPostComment(r_id:String, username: String, content: String, rating: Float) {
        class Data(
            @Json(index = 1) val canteenID: String,
            @Json(index = 2) val username: String,
            @Json(index = 3) val content: String,
            @Json(index = 4) val ranking: Double,
        )
        val url = IP.ipAddress + "canteens/postcomment"
        //val url = "http://10.68.104.199:8081/canteens/postcomment"
        Log.w("myTag11", "Here")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, JSONObject(Klaxon().toJsonString(Data(r_id, username, content, rating.toDouble()))),
            Response.Listener { response ->
                Log.w("myTag11", response.toString())
            }, Response.ErrorListener { error ->
                Log.w("myTag11", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)

    }
    private fun httpRefreshPage(ID: String) {
        val pageRating = findViewById<AndRatingBar>(R.id.restaurantPageRating)
        val pageCongestion = findViewById<AndRatingBar>(R.id.restaurantPageCongestion)
        val url_rating = IP.ipAddress + "canteens/ranking?canteenID=" + ID
        //val url_rating = "http://10.68.104.199:8081/canteens/ranking?canteenID=" + ID
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url_rating, null,
            Response.Listener { response ->
                Log.w("myTag", response.toString())
                pageRating.rating = response.get("ranking").toString().toFloat()
            }, Response.ErrorListener { error ->
                Log.w("myTag", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest1)
        val url_congestion = IP.ipAddress + "canteens/congestion?canteenID=" + ID
        //val url_congestion = "http://10.68.104.199:8081/canteens/congestion?canteenID=" + ID
        val jsonObjectRequest2 = JsonObjectRequest(
            Request.Method.GET, url_congestion, null,
            Response.Listener { response ->
                Log.w("myTag", response.toString())
                pageCongestion.rating = response.get("ranking").toString().toFloat()
            }, Response.ErrorListener { error ->
                Log.w("myTag", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest2)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun httpRefreshComment(ID: String) {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.commentSwipeRefresh)
        val url = IP.ipAddress +"canteens/comments?canteenID=" + ID
        //val url = "http://10.68.104.199:8081/canteens/comments?canteenID=" + ID
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.w("myTag1", response.toString())

                commentList = ArrayList()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)

                    val c_user = item.get("username").toString()
                    val c_rating = item.get("ranking").toString().toFloat()

                    val c_time_raw = item.get("time").toString()
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    format.timeZone = TimeZone.getTimeZone("UTC")  //In alignment with MongoDB UTC timezone.
                    val date: Date = format.parse(c_time_raw)
                    format.timeZone = TimeZone.getDefault()  // Switch back to HKT
                    format.applyPattern("yyyy-MM-dd HH:mm:ss z")
                    val formattedDate: String = format.format(date)


                    val c_content = item.get("content").toString()
                    commentList.add(CommentData(c_user, c_rating, formattedDate, c_content))
                }
                recv = findViewById(R.id.commentRecycler)
                commentAdapter = CommentAdapter(this,commentList)
                recv.layoutManager = LinearLayoutManager(this)
                recv.setHasFixedSize(false)
                recv.adapter = commentAdapter

            }, Response.ErrorListener { error ->
                Log.w("myTag1", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonArrayRequest)
        swipeRefresh.isRefreshing = false

    }

    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
            //Toast.makeText(applicationContext, "Please wait, it may take a few minute...",     Toast.LENGTH_SHORT).show()
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