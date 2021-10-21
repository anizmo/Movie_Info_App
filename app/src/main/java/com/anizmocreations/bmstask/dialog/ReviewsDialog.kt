package com.anizmocreations.bmstask.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.util.Utility
import com.anizmocreations.bmstask.adapter.ReviewsAdapter
import com.anizmocreations.bmstask.model.Review
import com.anizmocreations.bmstask.model.ReviewsResponse
import com.anizmocreations.bmstask.service.IMoviesService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.reviews_dialog_fragment.*

class ReviewsDialog(var reviewsResponse: ReviewsResponse?) : DialogFragment() {

    private var page = 1

    var reviewsAdapter : ReviewsAdapter ?= null

    var reviewsList: ArrayList<Review>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reviews_dialog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.FullScreenDialogAnimation

        reviewsList = reviewsResponse?.results

        if(context!=null){
            reviews_list_detail.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            reviewsAdapter = ReviewsAdapter(context!!, reviewsResponse?.results, true)
            reviews_list_detail.adapter = reviewsAdapter
        }

        reviews_list_detail.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if(reviewsResponse?.total_pages?:0 > page){
                        page++
                        fetchReviews()
                    }
                }
            }
        })
    }

    private fun fetchReviews() {
        val iMoviesService = Utility.getInstanceOfRetrofit().create(IMoviesService::class.java)

        iMoviesService?.getReviews(reviewsResponse?.id!!, page)?.subscribeOn(Schedulers.io())
            ?.retry(3)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                if(it.total_results == 0){
                    reviews_list_detail.visibility = View.GONE
                    review_list_label.visibility = View.GONE
                }else{
                    reviews_list_detail.visibility = View.VISIBLE
                    review_list_label.visibility = View.VISIBLE
                    reviewsList?.addAll(it.results!!)
                }
            }, {
                it.printStackTrace()
            }, {})
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

}