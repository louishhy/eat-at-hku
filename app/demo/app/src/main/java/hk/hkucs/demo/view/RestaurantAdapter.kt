package hk.hkucs.demo.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import hk.hkucs.demo.PageActivity
import hk.hkucs.demo.R
import hk.hkucs.demo.RestaurantActivity
import hk.hkucs.demo.global.IP
import hk.hkucs.demo.model.RestaurantData
import org.json.JSONObject
import per.wsj.library.AndRatingBar

@Suppress("RedundantSamConstructor")
class RestaurantAdapter(val c:Context, val restaurantList:ArrayList<RestaurantData>, val username: String?):RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(val v:View):RecyclerView.ViewHolder(v){
        //var r_image:ImageView
        var r_name:TextView
        var r_address:TextView
        var r_rating: AndRatingBar
        var r_congestion: AndRatingBar
        var r_update:Button
        var r_infopage:Button
        var r_image:ImageView

        init {
            //r_image = v.findViewById<ImageView>(R.id.restaurantImage)
            r_name = v.findViewById<TextView>(R.id.restaurantName)
            r_address = v.findViewById<TextView>(R.id.restaurantAddress)
            r_rating = v.findViewById<AndRatingBar>(R.id.restaurantItemRating)
            r_congestion = v.findViewById<AndRatingBar>(R.id.restaurantItemCongestion)
            r_update = v.findViewById<Button>(R.id.restaurantUpdateButton)
            r_infopage = v.findViewById<Button>(R.id.restaurantPage)
            r_image = v.findViewById<ImageView>(R.id.restaurantItemImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.restaurant_item,parent,false)
        return RestaurantViewHolder(v)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val newList = restaurantList[position]
        holder.r_name.text = newList.restaurantName
        holder.r_address.text = newList.restaurantAddress
        holder.r_rating.rating = newList.restaurantRating
        holder.r_congestion.rating = newList.restaurantCongestion
        holder.r_update.tag = "Update".plus(newList.restaurantName)
        holder.r_update.setOnClickListener() {

            val v = LayoutInflater.from(c).inflate(R.layout.update_item,null)
            val which = v.findViewById<TextView>(R.id.updateWhichRestaurant)
            val bar = v.findViewById<RatingBar>(R.id.congestionBar)
            val addDialog = androidx.appcompat.app.AlertDialog.Builder(c, R.style.AlertDialogTheme)

            which.text = newList.restaurantName.plus("by")

            addDialog.setView(v)
            addDialog.setPositiveButton("Upload"){ dialog,_->
                if (bar.rating >= 0.5f) {
                    //update action by username
                    httpUpdateRestaurantCongestion(newList.restaurantID, bar.rating)
                    dialog.dismiss()
                }
            }
            addDialog.create()
            addDialog.show()
        }
        holder.r_infopage.setOnClickListener() {
            val intent = Intent(c, RestaurantActivity::class.java)
            intent.putExtra("r_id",newList.restaurantID)
            intent.putExtra("username",username)
            intent.putExtra("name",newList.restaurantName)
            intent.putExtra("address",newList.restaurantAddress)
            c.startActivity(intent)
            (c as PageActivity).overridePendingTransition(R.anim.left_enter,R.anim.left_exit)
        }

        //image
        val url = IP.ipAddress + "canteens/canteenSmallImage?canteenID=" + newList.restaurantID
        //val url = "http://10.68.104.199:8081/canteens/canteenLargeImage?canteenID=" + newList.restaurantID
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val ret = response.get("url").toString()
                //Log.w("myTagImage", "http://10.68.104.199:8081/" + ret)
                DownloadImageFromInternet(holder.r_image).execute("http://10.68.104.199:8081/" + ret)
                //Log.w("myTagImage", response.toString())
            }, Response.ErrorListener { error ->
                Log.w("myTagImage", error.toString())
            }
        )
        Volley.newRequestQueue(c).add(jsonObjectRequest)

        //

    }

    override fun getItemCount(): Int {
        return  restaurantList.size
    }

    private fun httpUpdateRestaurantCongestion(ID: String, rating: Float) {
        class Data(
            @Json(index = 1) val canteenID: String,
            @Json(index = 2) val userID: String,
            @Json(index = 2) val congestionRanking: Double
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
        Volley.newRequestQueue(c).add(jsonObjectRequest)

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