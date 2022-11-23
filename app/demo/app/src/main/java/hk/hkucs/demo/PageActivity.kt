package hk.hkucs.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hk.hkucs.demo.model.RestaurantData
import hk.hkucs.demo.view.RestaurantAdapter

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
                refresh()
            } else {
                // The switch is disabled
                orderText.text = "By Rating"
                refresh()
            }

        }

        refresh()

        swipeRefresh.setOnRefreshListener() {
            refresh()
            swipeRefresh.isRefreshing = false
        }

    }
    private fun refresh() {
        restaurantList = ArrayList()
        for (i in 0..20) {
            restaurantList.add(RestaurantData("Restaurant Name ".plus(i), "Address".plus(i), 3.0f, 2.0f))
        }
        recv = findViewById(R.id.mRecycler)
        restaurantAdapter = RestaurantAdapter(this, restaurantList, username)
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = restaurantAdapter

    }
}