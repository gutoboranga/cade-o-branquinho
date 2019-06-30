package com.example.augusto.cade_o_branquinho.fragments.times_view_pager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.adapters.TimesAdapter
import com.example.augusto.cade_o_branquinho.model.DayType
import com.example.augusto.cade_o_branquinho.utils.DepartureTimes
import kotlinx.android.synthetic.main.week_days_times_layout.view.*


class WeekDaysTimesFragment : Fragment() {

    private lateinit var adapter: TimesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TimesAdapter(activity!!, DayType.WEEKDAY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.week_days_times_layout, container, false)

        view.week_days_times_list_item_recycler.adapter = adapter
        view.week_days_times_list_item_recycler.layoutManager = LinearLayoutManager(context)

        return view
    }

    fun updateNextDeparture() {
        if (::adapter.isInitialized) {
            adapter.updateNextDeparture()
        }
    }

}
