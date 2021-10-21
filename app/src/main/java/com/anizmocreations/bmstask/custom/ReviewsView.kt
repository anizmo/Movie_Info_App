package com.anizmocreations.bmstask.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.adapter.CastAdapter
import com.anizmocreations.bmstask.adapter.ReviewsAdapter
import com.anizmocreations.bmstask.model.CastAndCrewResponse
import com.anizmocreations.bmstask.model.ReviewsResponse
import kotlinx.android.synthetic.main.cast_and_crew_view.view.*

class ReviewsView : CoordinatorLayout {

    private var view : View? = null

    constructor(context: Context) : super(context) {
        setupView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView(context)
    }

    private fun setupView(context: Context) {
        val inflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.cast_and_crew_view, this)
        cast_and_crew_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    fun showReviewsList(listener: ReviewsViewListener, reviewsResponse: ReviewsResponse?, limit: Int) {

        var listLimit = limit

        if(listLimit >= reviewsResponse?.results?.size?:0){
            listLimit = reviewsResponse?.results?.size?:0
        }

        val adapter = ReviewsAdapter(context,reviewsResponse?.results?.subList(0,listLimit), false)
        cast_and_crew_list.adapter = adapter
        visibility = View.VISIBLE
        show_all_button_title.text = context.getString(R.string.all_reviews)
        show_all_button.setOnClickListener {
            listener?.openCompleteReviews(reviewsResponse)
        }
    }

    interface ReviewsViewListener{

        fun openCompleteReviews(reviewsResponse: ReviewsResponse?)

    }

}
