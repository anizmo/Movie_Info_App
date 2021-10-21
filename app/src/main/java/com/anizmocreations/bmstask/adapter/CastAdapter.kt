package com.anizmocreations.bmstask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.util.Utility
import com.anizmocreations.bmstask.model.Cast
import com.bumptech.glide.Glide

class CastAdapter(
    var context: Context,
    var castList: MutableList<Cast>?
) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cast_crew, null, false))
    }

    override fun getItemCount(): Int {
        return castList?.size?:0
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.name.text = castList?.get(position)?.name
        holder.job.text = castList?.get(position)?.character

        if(castList?.get(position)?.profile_path!=null){
            Glide.with(context)
                .load(Utility.IMAGE_BASE_URL+castList?.get(position)?.profile_path)
                .placeholder(R.drawable.profile_placeholder)
                .into(holder.profilePhoto)
        }

    }

    class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.name)
        var job = itemView.findViewById<TextView>(R.id.job)
        var profilePhoto = itemView.findViewById<ImageView>(R.id.profile_photo)
    }

}