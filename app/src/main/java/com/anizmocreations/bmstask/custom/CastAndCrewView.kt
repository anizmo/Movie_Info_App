package com.anizmocreations.bmstask.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.adapter.CastAdapter
import com.anizmocreations.bmstask.model.CastAndCrewResponse
import kotlinx.android.synthetic.main.cast_and_crew_view.view.*

class CastAndCrewView : CoordinatorLayout {

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
        cast_and_crew_list.layoutManager = GridLayoutManager(context, 3)
    }

    fun showGrid(listener: CastAndCrewViewListener, castAndCrewResponse: CastAndCrewResponse?, limit: Int) {

        var listLimit = limit

        if(listLimit >= castAndCrewResponse?.cast?.size?:0){
            listLimit = castAndCrewResponse?.cast?.size?:0
        }

        val adapter = CastAdapter(context,castAndCrewResponse?.cast?.subList(0,listLimit))
        cast_and_crew_list.adapter = adapter
        visibility = View.VISIBLE
        show_all_button.setOnClickListener {
            listener?.openCompleteCastAndCrew(castAndCrewResponse)
        }
    }

    interface CastAndCrewViewListener{

        fun openCompleteCastAndCrew(castAndCrewResponse: CastAndCrewResponse?)

    }

}
