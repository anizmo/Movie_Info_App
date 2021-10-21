package com.anizmocreations.bmstask.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.adapter.MoviesAdapter
import com.anizmocreations.bmstask.model.Movie
import com.anizmocreations.bmstask.model.NowPlayingResponse
import com.anizmocreations.bmstask.model.SimilarMovieResponse
import com.anizmocreations.bmstask.service.IMoviesService
import com.anizmocreations.bmstask.util.ConnectionLiveData
import com.anizmocreations.bmstask.util.Utility
import com.anizmocreations.bmstask.util.Utility.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var movies: ArrayList<Movie>? = null

    private var onlineMovies: ArrayList<Movie>? = null

    private var moviesAdapter: MoviesAdapter?= null

    private var onlineMoviesAdapter: MoviesAdapter?= null

    private var page = 1

    private var onlineSearchPage = 1

    private var onlineSearchQuery = ""

    private var alert : AlertDialog? = null

    var iMoviesService: IMoviesService?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iMoviesService = Utility.getInstanceOfRetrofit().create(IMoviesService::class.java)

        val connectionLiveData = ConnectionLiveData(applicationContext)
        connectionLiveData.observe(this, androidx.lifecycle.Observer {
            if (!it.isConnected) {
                println("Not connected")
                alert?.show()
            } else {
                println("Connected")
                alert?.dismiss()
            }
        })

        setupBottomNav()
        setupNoNetworkDialog()
        setupMoviesList()
        setupLocalSearch()
        setupOnlineSearch()
        fetchNowPlayingList()
        setupOnlineMoviesList()
    }

    private fun setupOnlineSearch() {
        search_online.setOnEditorActionListener { textView: TextView, i: Int, keyEvent: KeyEvent? ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (search_online?.text != null && search_online.text.isNotEmpty()) {
                    placeholder.visibility = View.GONE
                    online_list.visibility = View.VISIBLE
                    onlineMovies?.clear()
                    onlineMoviesAdapter?.notifyDataSetChanged()

                    onlineSearchQuery = search_online?.text.toString()

                    callOnlineSearchAPI()

                }
                hideKeyboard(textView.context, textView.windowToken)
                search_online.clearFocus()
                true
            }
            false
        }
    }

    private fun callOnlineSearchAPI() {
        iMoviesService?.searchMovie(onlineSearchQuery, onlineSearchPage)
            ?.subscribeOn(Schedulers.io())
            ?.retry(3)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                setupOnlineMoviesList(it)
            }, {
                it.printStackTrace()
                if (onlineSearchPage > 1) {
                    onlineSearchPage--
                }
            }, {})
    }

    private fun setupLocalSearch() {
        search_local.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                performSearch(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })
    }

    private fun setupMoviesList() {
        movies = ArrayList()
        moviesAdapter = MoviesAdapter(this, movies, false)

        movies_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    page++
                    fetchNowPlayingList()
                }
            }
        })

        movies_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        movies_list.adapter = moviesAdapter
    }

    private fun setupOnlineMoviesList() {
        onlineMovies = ArrayList()
        onlineMoviesAdapter = MoviesAdapter(this, onlineMovies, false)

        online_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    onlineSearchPage++
                    callOnlineSearchAPI()
                }
            }
        })

        online_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        online_list.adapter = onlineMoviesAdapter
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

    private fun performSearch(query: String) {

        val searchResults = ArrayList<Movie>()

        val queryLists = query.trim().split(" ")

        var queryListSize = queryLists.size

        movies?.forEach { movie ->
            var wordsSearchesFound = 0
            queryLists.forEach { queryPart ->
                if(!TextUtils.isEmpty(queryPart)){
                    val titleWords = movie.title?.split(" ")
                    titleWords?.forEach { title->
                        if(title.startsWith(queryPart, true)){
                            wordsSearchesFound++
                        }
                    }
                }else{
                    queryListSize--
                }
            }

            if(wordsSearchesFound == queryListSize){
                if(!searchResults.contains(movie)){
                    searchResults.add(movie)
                }
            }
        }

        if(TextUtils.isEmpty(query)){
            moviesAdapter?.setList(movies)
        }else{
            moviesAdapter?.setList(searchResults)
        }

    }

    private fun setupBottomNav() {
        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_now_playing -> {
                    now_playing_frame.visibility = View.VISIBLE
                    search_online_frame.visibility = View.GONE
                    true
                }
                R.id.navigation_global_search -> {
                    now_playing_frame.visibility = View.GONE
                    search_online_frame.visibility = View.VISIBLE
                    true
                }
                else -> {
                    false
                }
            }
        }

        navigation.setOnNavigationItemReselectedListener {
            when(it.itemId) {
                R.id.navigation_now_playing -> {
                    movies_list.smoothScrollToPosition(0)
                }
            }
        }
    }

    private fun fetchNowPlayingList(){
        iMoviesService?.getNowPlayingMovies(page)?.subscribeOn(Schedulers.io())
            ?.retry(3)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                if(page == 1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        }
                        window.statusBarColor = ContextCompat.getColor(this@MainActivity ,
                            R.color.white
                        )
                    }
                }
                setupNowPLayingMoviesList(it)
            }, {
                it.printStackTrace()
                if(page > 1){
                    page--
                }
            }, {})

    }

    private fun setupNowPLayingMoviesList(response: NowPlayingResponse) {
        if(response.results!=null){
            movies?.addAll(response.results!!)
            moviesAdapter?.notifyDataSetChanged()
        }

        if(page == 1){
            TransitionManager.beginDelayedTransition(splash_image)
            splash_image.visibility = View.GONE
            navigation.visibility = View.VISIBLE
        }
    }

    private fun setupOnlineMoviesList(response: SimilarMovieResponse?) {
        if(response?.results!=null){
            onlineMovies?.addAll(response.results!!)
            onlineMoviesAdapter?.notifyDataSetChanged()
        }
    }

}