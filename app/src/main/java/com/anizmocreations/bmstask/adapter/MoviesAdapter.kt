package com.anizmocreations.bmstask.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.util.Utility
import com.anizmocreations.bmstask.activity.MovieDetailActivity
import com.anizmocreations.bmstask.model.Movie
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MoviesAdapter(
    var context: Context,
    var moviesList: ArrayList<Movie>?,
    var isSimilarMoviesAdapter: Boolean
) : RecyclerView.Adapter<MoviesAdapter.NowPLayingAdapterViewHolder>() {

    private val simpleDateFormat = SimpleDateFormat("yyyy-mm-dd")
    private val requiredSimepleDateFormat = SimpleDateFormat("d MMMM yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowPLayingAdapterViewHolder {
        return NowPLayingAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_movie_now_playing, null, false))
    }

    override fun getItemCount(): Int {
        return moviesList?.size?:0
    }

    override fun onBindViewHolder(holder: NowPLayingAdapterViewHolder, position: Int) {


        val loc = Locale(moviesList?.get(position)?.original_language?:"en")
        val language_display_name: String = loc.getDisplayLanguage(loc)

        holder.languageAndRating.text = language_display_name

        if(moviesList?.get(position)?.poster_path!=null){
            Glide.with(context)
                .load(Utility.IMAGE_BASE_URL+moviesList?.get(position)?.poster_path)
                .placeholder(R.drawable.film_poster_placeholder)
                .into(holder.moviePoster)
        }

        if(moviesList?.get(position)?.release_date!=null && !TextUtils.isEmpty(moviesList?.get(position)?.release_date)){
            val date = simpleDateFormat.parse(moviesList?.get(position)?.release_date)
            holder.movieReleaseDate.text = requiredSimepleDateFormat.format(date)
            holder.movieReleaseDate.visibility = View.VISIBLE
        }else{
            holder.movieReleaseDate.visibility = View.INVISIBLE
        }

        if(isSimilarMoviesAdapter){
            holder.movieTitle.text = moviesList?.get(position)?.title
            holder.movieOverview.visibility = View.GONE
        }else{
            holder.movieTitle.text = moviesList?.get(position)?.title
            holder.movieOverview.visibility = View.VISIBLE
            holder.movieOverview.text = moviesList?.get(position)?.overview
        }


        holder.itemView.setOnClickListener {
            val movieDetailActivityIntent = Intent(context, MovieDetailActivity::class.java)
            movieDetailActivityIntent.putExtra(Utility.MOVIE_OBJECT_KEY, moviesList?.get(position))
            context.startActivity(movieDetailActivityIntent)
        }
    }

    fun setList(newMoviesList: ArrayList<Movie>?){
        moviesList = newMoviesList
        notifyDataSetChanged()
    }

    class NowPLayingAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var movieTitle = itemView.findViewById<TextView>(R.id.movie_title)
        var movieReleaseDate = itemView.findViewById<TextView>(R.id.movie_release_date)
        var movieOverview = itemView.findViewById<TextView>(R.id.movie_overview)
        var moviePoster = itemView.findViewById<ImageView>(R.id.movie_poster)
        var languageAndRating = itemView.findViewById<TextView>(R.id.language_and_rating)
    }

}