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
import hk.hkucs.demo.model.CommentData
import hk.hkucs.demo.model.RestaurantData
import per.wsj.library.AndRatingBar

class CommentAdapter(val c:Context,val commentList:ArrayList<CommentData>):RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val v:View):RecyclerView.ViewHolder(v){

        var c_name:TextView
        var c_time:TextView
        var c_rating:AndRatingBar
        var c_context:TextView

        init {
            c_name = v.findViewById<TextView>(R.id.commenterName)
            c_time = v.findViewById<TextView>(R.id.commentTime)
            c_rating = v.findViewById<AndRatingBar>(R.id.commentRating)
            c_context = v.findViewById<TextView>(R.id.commentContext)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.comment_item,parent,false)
        return CommentViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val newList = commentList[position]
        holder.c_name.text = newList.commenterName
        holder.c_rating.rating = newList.commentRating
        holder.c_context.text = newList.commentContext
        holder.c_time.text = newList.commentTime

    }

    override fun getItemCount(): Int {
        return  commentList.size
    }

}