package com.anizmocreations.bmstask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.model.Movie
import com.anizmocreations.bmstask.model.Review

class ReviewsAdapter(
    var context: Context,
    var reviewsList: MutableList<Review>?,
    var isDetail: Boolean?
) : RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {

    class ReviewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var author = itemView.findViewById<TextView>(R.id.review_author)
        var content = itemView.findViewById<TextView>(R.id.review_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        return ReviewsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_review, null, false)
        )
    }

    override fun getItemCount(): Int {
        return reviewsList?.size?:0
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.author.text = reviewsList?.get(position)?.author
        holder.content.text = reviewsList?.get(position)?.content

        if(isDetail == true){
            holder.content.maxLines = Int.MAX_VALUE
            holder.content.ellipsize = null
        }
    }

}