package com.example.augusto.cade_o_branquinho.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.augusto.cade_o_branquinho.R
import kotlinx.android.synthetic.main.times_fragment_layout.view.*
import com.example.augusto.cade_o_branquinho.adapters.SectionsPagerAdapter


class TimesFragment : Fragment() {

    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null

    private var sectionsPagerAdapter: SectionsPagerAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.times_fragment_layout, container, false)

        sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)

        viewPager = view.times_fragment_view_pager as ViewPager
        viewPager!!.adapter = sectionsPagerAdapter

        val tabLayout = view.times_fragment_tab_layout as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

}
