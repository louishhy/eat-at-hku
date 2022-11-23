package hk.hkucs.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ImageActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val extraDataRestaurantName = intent.getStringExtra("restaurant")
        val username = intent.getStringExtra("username")
        val extraDataRestaurantAddress = intent.getStringExtra("address")

        val backButton = findViewById<Button>(R.id.imagePageBackButton)
        val title = findViewById<TextView>(R.id.imagePageTitle)

        title.text = extraDataRestaurantName

        backButton.setOnClickListener() {
            val intent = Intent(this@ImageActivity, RestaurantActivity::class.java)
            intent.putExtra("username",username)
            intent.putExtra("name",extraDataRestaurantName)
            intent.putExtra("address",extraDataRestaurantAddress)
            startActivity(intent)
            overridePendingTransition(R.anim.right_enter,R.anim.right_exit)
        }

    }

}