package hk.hkucs.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hk.hkucs.demo.model.CommentData
import hk.hkucs.demo.model.RestaurantData
import hk.hkucs.demo.view.CommentAdapter
import hk.hkucs.demo.view.RestaurantAdapter
import per.wsj.library.AndRatingBar


class RestaurantActivity : AppCompatActivity() {

    private lateinit var recv: RecyclerView
    private lateinit var commentList:ArrayList<CommentData>
    private lateinit var commentAdapter: CommentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
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
        pageRating.rating = 2.5f
        pageCongestion.rating = 3.5f
        pageDescription.text = "Name: ".plus(extraDataRestaurantName.plus("\nAddress: ".plus(extraDataRestaurantAddress)))

        backToPageButton.setOnClickListener() {
            val tmp = Intent(this@RestaurantActivity, PageActivity::class.java)
            startActivity(tmp)
            overridePendingTransition(R.anim.right_enter,R.anim.right_exit)
        }
        cardInfo.setOnClickListener() {
            val intent = Intent(this@RestaurantActivity, ImageActivity::class.java)
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
                    dialog.dismiss()
                }
            }
            addDialog.create()
            addDialog.show()

        }

        refresh()

        swipeRefresh.setOnRefreshListener () {
            refresh()
            swipeRefresh.isRefreshing = false
        }

        postCommentButton.setOnClickListener() {
            val p = LayoutInflater.from(this).inflate(R.layout.postcomment_item,null)
            val bar = p.findViewById<RatingBar>(R.id.ratingBar)
            val context = p.findViewById<EditText>(R.id.postCommentContext)
            val postDialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

            postDialog.setView(p)
            postDialog.setPositiveButton("Upload"){ dialog,_->
                if (bar.rating >= 0.5f && context.text != null) {
                    //post action
                    dialog.dismiss()
                }
            }
            postDialog.create()
            postDialog.show()
        }

    }
    private fun refresh() {

        commentList = ArrayList()
        for (i in 0..20) {
            commentList.add(CommentData("Commenter Name".plus(i), 2.5f, "00:00 00/00/0000", "Comment Context\n"))
        }
        recv = findViewById(R.id.commentRecycler)
        commentAdapter = CommentAdapter(this,commentList)
        recv.layoutManager = LinearLayoutManager(this)
        //recv.isNestedScrollingEnabled = false
        recv.setHasFixedSize(false)
        recv.adapter = commentAdapter
    }

}