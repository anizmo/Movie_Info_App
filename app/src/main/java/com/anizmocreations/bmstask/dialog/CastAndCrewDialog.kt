package com.anizmocreations.bmstask.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.anizmocreations.bmstask.R
import com.anizmocreations.bmstask.adapter.CastAdapter
import com.anizmocreations.bmstask.adapter.CrewAdapter
import com.anizmocreations.bmstask.model.CastAndCrewResponse
import kotlinx.android.synthetic.main.cast_and_crew_dialog_fragment.*
import kotlinx.android.synthetic.main.cast_and_crew_view.view.*

class CastAndCrewDialog(var castAndCrewResponse: CastAndCrewResponse?) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cast_and_crew_dialog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.FullScreenDialogAnimation

        if(context!=null){
            cast_grid.layoutManager = GridLayoutManager(context,3)
            val castAdapter = CastAdapter(context!!, castAndCrewResponse?.cast)
            cast_grid.adapter = castAdapter

            crew_grid.layoutManager = GridLayoutManager(context,3)
            val crewAdapter = CrewAdapter(context!!, castAndCrewResponse?.crew)
            crew_grid.adapter = crewAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

}