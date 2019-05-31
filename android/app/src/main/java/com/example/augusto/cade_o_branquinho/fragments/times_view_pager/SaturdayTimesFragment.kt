package com.example.augusto.cade_o_branquinho.fragments.times_view_pager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import com.example.augusto.cade_o_branquinho.adapters.TimesAdapter
import com.example.augusto.cade_o_branquinho.utils.DepartureTimesUtils
import kotlinx.android.synthetic.main.saturday_times_layout.view.*
import kotlinx.android.synthetic.main.week_days_times_layout.view.*


class SaturdayTimesFragment : Fragment() {

    private var adapter: TimesAdapter? = null
    private var weekDays = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parser = DepartureTimesUtils(context!!)
        weekDays = parser.getSaturdays() ?: arrayListOf()

        adapter = TimesAdapter(activity!!, weekDays)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.saturday_times_layout, container, false)

        view.saturday_times_list_item_recycler.adapter = adapter
        view.saturday_times_list_item_recycler.layoutManager = LinearLayoutManager(context)

        return view
    }

}
