package com.example.augusto.cade_o_branquinho.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.augusto.cade_o_branquinho.fragments.times_view_pager.SaturdayTimesFragment
import com.example.augusto.cade_o_branquinho.fragments.times_view_pager.WeekDaysTimesFragment


class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val weekDaysTimesFragment = WeekDaysTimesFragment()
    private val saturdayTimesFragment = SaturdayTimesFragment()

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return weekDaysTimesFragment
            1 -> return saturdayTimesFragment
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "SEGUNDA A SEXTA"
            1 -> return "SÁBADO"
        }
        return null
    }
}