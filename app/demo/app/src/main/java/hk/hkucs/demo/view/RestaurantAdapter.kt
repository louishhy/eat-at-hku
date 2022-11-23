package hk.hkucs.demo.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import hk.hkucs.demo.PageActivity
import hk.hkucs.demo.R
import hk.hkucs.demo.RestaurantActivity
import hk.hkucs.demo.model.RestaurantData
import per.wsj.library.AndRatingBar

class RestaurantAdapter(val c:Context,val restaurantList:ArrayList<RestaurantData>, val username: String?):RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(val v:View):RecyclerView.ViewHolder(v){
        //var r_image:ImageView
        var r_name:TextView
        var r_address:TextView
        var r_rating: AndRatingBar
        var r_congestion: AndRatingBar
        var r_update:Button
        var r_infopage:Button

        init {
            //r_image = v.findViewById<ImageView>(R.id.restaurantImage)
            r_name = v.findViewById<TextView>(R.id.restaurantName)
            r_address = v.findViewById<TextView>(R.id.restaurantAddress)
            r_rating = v.findViewById<AndRatingBar>(R.id.restaurantItemRating)
            r_congestion = v.findViewById<AndRatingBar>(R.id.restaurantItemCongestion)
            r_update = v.findViewById<Button>(R.id.restaurantUpdateButton)
            r_infopage = v.findViewById<Button>(R.id.restaurantPage)
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
                    dialog.dismiss()
                }
            }
            addDialog.create()
            addDialog.show()
        }
        holder.r_infopage.setOnClickListener() {
            val intent = Intent(c, RestaurantActivity::class.java)
            intent.putExtra("username",username)
            intent.putExtra("name",newList.restaurantName)
            intent.putExtra("address",newList.restaurantAddress)
            c.startActivity(intent)
            (c as PageActivity).overridePendingTransition(R.anim.left_enter,R.anim.left_exit)
        }

    }

    override fun getItemCount(): Int {
        return  restaurantList.size
    }
    /*
    private fun updateRestaurantCongestion() {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.update_item,parent,false)

    }*/
}