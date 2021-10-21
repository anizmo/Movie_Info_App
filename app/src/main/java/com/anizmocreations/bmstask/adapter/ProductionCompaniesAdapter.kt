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
import com.anizmocreations.bmstask.model.ProductionCompany
import com.bumptech.glide.Glide

class ProductionCompaniesAdapter(
    var context: Context,
    var prodCompList: MutableList<ProductionCompany>?
) : RecyclerView.Adapter<ProductionCompaniesAdapter.ProductionCompanyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionCompanyViewHolder {
        return ProductionCompanyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_production_company, null, false))
    }

    override fun getItemCount(): Int {
        return prodCompList?.size?:0
    }

    override fun onBindViewHolder(holder: ProductionCompanyViewHolder, position: Int) {
        holder.name.text = prodCompList?.get(position)?.name

        if(prodCompList?.get(position)?.logo_path!=null){
            Glide.with(context)
                .load(Utility.IMAGE_BASE_URL+prodCompList?.get(position)?.logo_path)
                .placeholder(R.drawable.film_poster_placeholder)
                .into(holder.logo)
        }

    }

    class ProductionCompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.name)
        var logo = itemView.findViewById<ImageView>(R.id.profile_photo)
    }

}