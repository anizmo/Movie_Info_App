package com.anizmocreations.bmstask.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.adapter.MoviesAdapter
import com.anizmocreations.bmstask.adapter.ProductionCompaniesAdapter
import com.anizmocreations.bmstask.custom.CastAndCrewView
import com.anizmocreations.bmstask.custom.ReviewsView
import com.anizmocreations.bmstask.dialog.CastAndCrewDialog
import com.anizmocreations.bmstask.dialog.ReviewsDialog
import com.anizmocreations.bmstask.model.CastAndCrewResponse
import com.anizmocreations.bmstask.model.Movie
import com.anizmocreations.bmstask.model.MovieDetailsResponse
import com.anizmocreations.bmstask.model.ReviewsResponse
import com.anizmocreations.bmstask.service.IMoviesService
import com.anizmocreations.bmstask.util.ConnectionLiveData
import com.anizmocreations.bmstask.util.Utility
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movie_detail.*
import java.text.SimpleDateFormat


class MovieDetailActivity : AppCompatActivity(), CastAndCrewView.CastAndCrewViewListener,
    ReviewsView.ReviewsViewListener {

    private var movie: Movie? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy-mm-dd")
    private val requiredSimepleDateFormat = SimpleDateFormat("d MMMM yyyy")
    private var iMoviesService: IMoviesService ? = null

    private var similarMoviesAdapter: MoviesAdapter?= null
    private var similarMoviesList: ArrayList<Movie>?= null

    private var similarMoviesPage = 1
    private var similarMoviesTotalPageCount = 1

    private var alert : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_movie_detail)

        iMoviesService = Utility.getInstanceOfRetrofit().create(IMoviesService::class.java)

        movie = intent.getSerializableExtra(Utility.MOVIE_OBJECT_KEY) as Movie?

        val connectionLiveData = ConnectionLiveData(applicationContext)
        connectionLiveData.observe(this, androidx.lifecycle.Observer {
            if (!it.isConnected) {
                alert?.show()
                println("Not connected")
            } else {
                alert?.dismiss()
                println("Connected")
            }
        })

        initialiseSimilarMoviesRecyclerView()

        setupMovieDetailPageLocal()
        fetchMovieDetails()
        fetchReviews()
        fetchSimilarMovies()
        fetchCredits()
        setupNoNetworkDialog()
    }

    private fun setupNoNetworkDialog(){
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("This app requires an internet connection to work, switch to a network to continue.")
            .setCancelable(false)

            .setNegativeButton("Exit") { dialog, id ->
                finishAffinity()
            }
        alert = dialogBuilder.create()
    }

    private fun fetchMovieDetails() {
        iMoviesService?.getDetails(movie?.id!!)?.subscribeOn(Schedulers.io())
            ?.retry(3)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                setupMovieDetails(it)
            }, {
                it.printStackTrace()
            }, {})
    }

    private fun setupMovieDetails(movieDetailsResponse: MovieDetailsResponse?){
        movieDetailsResponse?.genres?.forEach {
            movie_genre.text = "${movie_genre.text} •${it.name}"
        }

        if(movieDetailsResponse?.runtime!=null){
            val hours: Int = movieDetailsResponse?.runtime!! / 60
            val minutes: Int = movieDetailsResponse?.runtime!! % 60
            duration.text = "$hours hr $minutes min"
        }
        production_company.layoutManager = GridLayoutManager(this, 3)
        production_company.adapter = ProductionCompaniesAdapter(this, movieDetailsResponse?.production_companies)
    }

    private fun initialiseSimilarMoviesRecyclerView() {
        similar_movies_list.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        similarMoviesList = ArrayList()

        similar_movies_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollHorizontally(1)) {
                    if(similarMoviesTotalPageCount > similarMoviesPage){
                        similarMoviesPage++
                        fetchSimilarMovies()
                    }
                }
            }
        })

        similarMoviesAdapter = MoviesAdapter(this, similarMoviesList, true)
        similar_movies_list.adapter = similarMoviesAdapter
    }

    private fun fetchCredits() {
        iMoviesService?.getCredits(movie?.id!!)?.subscribeOn(Schedulers.io())
            ?.retry(3)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                setupCredits(it)
            }, {
                it.printStackTrace()
            }, {})
    }

    private fun setupCredits(it: CastAndCrewResponse?) {
        cast_and_crew_view.showGrid(this, it,6)
    }

    private fun fetchSimilarMovies() {
        iMoviesService?.getSimilar(movie?.id!!, similarMoviesPage)?.subscribeOn(Schedulers.io())
            ?.retry(3)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                if(it.total_results == 0){
                    similar_movies_list.visibility = View.GONE
                    similar_movies_list_label.visibility = View.GONE
                }else{
                    similarMoviesTotalPageCount = it.total_pages!!
                    similar_movies_list.visibility = View.VISIBLE
                    similar_movies_list_label.visibility = View.VISIBLE
                    setupSimilarMovies(it.results)
                }
            }, {
                it.printStackTrace()
                if(similarMoviesPage > 1){
                    similarMoviesPage--
                }
            }, {})
    }

    private fun setupSimilarMovies(similarMovies: ArrayList<Movie>?) {
        if(similarMovies!=null){
            similarMoviesList?.addAll(similarMovies)
            similarMoviesAdapter?.notifyDataSetChanged()
        }
    }

    private fun fetchReviews() {
        iMoviesService?.getReviews(movie?.id!!, 1)?.subscribeOn(Schedulers.io())
            ?.retry(3)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                if(it.total_results == 0){
                    reviews_view.visibility = View.GONE
                    review_list_label.visibility = View.GONE
                }else{
                    reviews_view.visibility = View.VISIBLE
                    review_list_label.visibility = View.VISIBLE
                    setupReviews(it)
                }
            }, {
                it.printStackTrace()
            }, {})
    }

    private fun setupReviews(reviewsResponse: ReviewsResponse) {
        if(reviewsResponse.results!=null){
            reviews_view.showReviewsList(this,reviewsResponse,3)
        }
    }

    private fun setupMovieDetailPageLocal() {
        Glide.with(this)
            .load(Utility.IMAGE_BASE_URL + movie?.backdrop_path)
            .into(movie_backdrop)

        val date = simpleDateFormat.parse(movie?.release_date ?: "")
        movie_subtitle_detail.text = requiredSimepleDateFormat.format(date)

        movie_title_detail.text = movie?.title
        movie_overview_detail.text = movie?.overview
    }

    override fun openCompleteCastAndCrew(castAndCrewResponse: CastAndCrewResponse?) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("castAndCrewFragment")
        if (prev != null) {
            ft.remove(prev)
        }
        val castAndCrewDialogFragment = CastAndCrewDialog(castAndCrewResponse)
        castAndCrewDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyAppDialog)
        castAndCrewDialogFragment.show(ft, "castAndCrewFragment")
    }

    override fun openCompleteReviews(reviewsResponse: ReviewsResponse?) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("reviewsFragment")
        if (prev != null) {
            ft.remove(prev)
        }
        val reviewsDialog = ReviewsDialog(reviewsResponse)
        reviewsDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyAppDialog)
        reviewsDialog.show(ft, "reviewsFragment")
    }
}