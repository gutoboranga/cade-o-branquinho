package com.example.augusto.cade_o_branquinho.fragments.times_view_pager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R

class WeekDaysTimesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.week_days_times_layout, container, false)
    }

}
