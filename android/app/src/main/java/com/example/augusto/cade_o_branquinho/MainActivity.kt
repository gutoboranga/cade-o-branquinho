package com.example.augusto.cade_o_branquinho

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.augusto.cade_o_branquinho.fragments.MapFragment
import com.example.augusto.cade_o_branquinho.fragments.TimesFragment
import com.example.augusto.cade_o_branquinho.fragments.WarningsFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    val mapFragment = MapFragment()
    val warningsFragment = WarningsFragment()
    val timesFragment = TimesFragment()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val transaction = supportFragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_map -> {
                transaction.replace(R.id.fragment_container, mapFragment)
            }
            R.id.navigation_warnings -> {
                transaction.replace(R.id.fragment_container, warningsFragment)
            }
            R.id.navigation_times -> {
                transaction.replace(R.id.fragment_container, timesFragment)
            }
        }

        transaction.commit()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(this)
        navigation.selectedItemId = R.id.navigation_map
    }

}
