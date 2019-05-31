package com.example.augusto.cade_o_branquinho

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.support.v7.widget.Toolbar
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
                showMapsToolbar()
                transaction.show(mapFragment)
                transaction.hide(warningsFragment)
                transaction.hide(timesFragment)
            }
            R.id.navigation_warnings -> {
                showWarningsToolbar()
                transaction.hide(mapFragment)
                transaction.show(warningsFragment)
                transaction.hide(timesFragment)
            }
            R.id.navigation_times -> {
                showTimesToolbar()
                transaction.hide(mapFragment)
                transaction.hide(warningsFragment)
                transaction.show(timesFragment)
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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, mapFragment)
        transaction.add(R.id.fragment_container, warningsFragment)
        transaction.add(R.id.fragment_container, timesFragment)
        transaction.commit()

        navigation.selectedItemId = R.id.navigation_map
    }

    // --- Toolbar functions ---

    fun showMapsToolbar() {
        supportActionBar!!.elevation = applicationContext.resources.getDimension(R.dimen.tab_layout_elevation)
        supportActionBar!!.title = "Cadê o branquinho?"
    }

    fun showWarningsToolbar() {
        supportActionBar!!.elevation = applicationContext.resources.getDimension(R.dimen.tab_layout_elevation)
        supportActionBar!!.title = "Avisos"
    }

    fun showTimesToolbar() {
        supportActionBar!!.elevation = 0F
        supportActionBar!!.title = "Horários de partida"
    }

}
