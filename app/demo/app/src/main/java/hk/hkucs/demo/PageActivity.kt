package hk.hkucs.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hk.hkucs.demo.model.RestaurantData
import hk.hkucs.demo.view.RestaurantAdapter
import org.json.JSONObject

@Suppress("RedundantSamConstructor")
class PageActivity : AppCompatActivity() {

    private lateinit var recv: RecyclerView
    private lateinit var restaurantList:ArrayList<RestaurantData>
    private lateinit var restaurantAdapter: RestaurantAdapter
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)

        username = intent.getStringExtra("username")

        val logOut = findViewById<Button>(R.id.logOutButton)
        val orderBy = findViewById<Switch>(R.id.orderBySwitch)
        val orderText = findViewById<TextView>(R.id.orderByTextView)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefresh)

        logOut.setOnClickListener() {
            val tmp = Intent(this@PageActivity, MainActivity::class.java)
            startActivity(tmp)
            overridePendingTransition(R.anim.right_enter,R.anim.right_exit)
        }

        orderBy.setOnCheckedChangeListener() { buttonView, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                orderText.text = "By Congestion"
                //refresh("congestion")
                httpRefresh("congestion")
            } else {
                // The switch is disabled
                orderText.text = "By Rating"
                //refresh("ranking")
                httpRefresh("ranking")
            }

        }

        //refresh("ranking")
        httpRefresh("ranking")

        swipeRefresh.setOnRefreshListener() {
            if (orderText.text == "By Rating") {
                //refresh("ranking")
                httpRefresh("ranking")
            } else {
                //refresh("congestion")
                httpRefresh("congestion")
            }

        }

    }

    private fun httpRefresh(order: String) {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefresh)
        val url = "http://10.68.104.199:8081/canteens/get_all_canteens" + "?sortby=" + order
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                //Log.w("myTag1", response.toString())

                restaurantList = ArrayList()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val r_id = item.get("_id").toString()
                    val name = item.get("canteenName").toString()
                    val address = item.get("address").toString()
                    val rating = item.get("ranking").toString().toFloat()
                    val con = item.get("congestionRanking").toString().toFloat()
                    restaurantList.add(RestaurantData(r_id, name, address, rating, con))
                }
                recv = findViewById(R.id.mRecycler)
                restaurantAdapter = RestaurantAdapter(this, restaurantList, username)
                recv.layoutManager = LinearLayoutManager(this)
                recv.adapter = restaurantAdapter

                swipeRefresh.isRefreshing = false

            }, Response.ErrorListener { error ->
                Log.w("myTag1", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }
    /*
    private fun refresh(order: String) {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefresh)
        restaurantList = ArrayList()
        for (i in 0..20) {
            restaurantList.add(RestaurantData("Restaurant Name ".plus(i), "Address".plus(i), 3.0f, 2.0f))
        }
        recv = findViewById(R.id.mRecycler)
        restaurantAdapter = RestaurantAdapter(this, restaurantList, username)
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = restaurantAdapter

        swipeRefresh.isRefreshing = false
    }*/
}