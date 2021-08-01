package com.homework.nasibullin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homework.nasibullin.fragments.MainFragment
import com.homework.nasibullin.fragments.MovieDetailsFragment
import com.homework.nasibullin.fragments.ProfileFragment
import com.homework.nasibullin.interfaces.MainFragmentCallbacks


class MainActivity : AppCompatActivity(), MainFragmentCallbacks {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var actionBottomFlag: Boolean = true
    private var currentFragment:String? = null
    private var currentMovieTitle:String? = null
    private var currentGenre:String? = null

    companion object {
        const val MAIN_FRAGMENT_TAG = "mainFragment"
        const val MOVIE_DETAIL_FRAGMENT_TAG = "movieDetailFragment"
        const val PROFILE_FRAGMENT_TAG = "profileFragment"
        const val CURRENT_FRAGMENT_KEY = "currentFragment"
        const val CURRENT_MOVIE_KEY = "currentMovie"
        const val CURRENT_MOVIE_GENRE = "currentGenre"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stateRestoration(savedInstanceState)
        initNavigationListener()
    }


    /**
     *  Get screen width to make margin between elements relative to the screen width
     *  @return screen width
     * */
    private val screenWidth: Int
    get() = windowManager.defaultDisplay.width

    private fun stateRestoration(savedInstanceState: Bundle?){
        if(savedInstanceState == null){
            addFragment(MAIN_FRAGMENT_TAG)
        }
        else{
            showLastFragment()
        }
    }

    /**
     * show last fragment after screen flipping
     */
    private fun showLastFragment(){
        supportFragmentManager.beginTransaction()
            .show(supportFragmentManager.fragments.last())
            .commit()
    }


    /**
     * Change bottom active item without actions. Need when Back pressed
        so that no action is performed when the button is changed
     */
    private fun changeBottomItemWithoutAction() {
        actionBottomFlag = false
        bottomNavigationView.selectedItemId = if (bottomNavigationView.selectedItemId == R.id.nav_profile) {
            R.id.nav_home
        } else {
            R.id.nav_profile
        }
    }

    /**
     * add fragment navigation on Back pressed
     */
    override fun onBackPressed() {

        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.clFragmentContainer)
        if (fragment != null && supportFragmentManager.fragments.size != 1) {
            fragmentTransaction.remove(fragment)
            fragmentTransaction.commit()
            if (fragment.tag != MOVIE_DETAIL_FRAGMENT_TAG) {
                changeBottomItemWithoutAction()
            }
        } else {
            super.onBackPressed()
        }
    }

    /**
     * init bottom navigation listener
     */
    private fun initNavigationListener() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        bottomNavigationView.setOnItemSelectedListener {
            actionBottom(it)
        }
    }

    /**
     * action which execute
     * @param item of bottom bar, which was pressed
     */
    private fun actionBottom(item: MenuItem): Boolean {
        if (actionBottomFlag) {
            when (item.itemId) {
                R.id.nav_home -> {
                    val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.clFragmentContainer)
                    if (fragment?.tag == MOVIE_DETAIL_FRAGMENT_TAG) {
                        fragmentTransaction.remove(fragment)
                        fragmentTransaction.commit()
                    }
                    addFragment(MAIN_FRAGMENT_TAG)
                }
                R.id.nav_profile -> addFragment(PROFILE_FRAGMENT_TAG)
                else -> return false
            }
        } else {
            actionBottomFlag = true
        }
        return true
    }

    /**
     * Checking whether a fragment was created with such a tag or not. If yes, then delete the fragment to recreate it
     * @param tag of fragment, which need to check
     */
    private fun checkFragmentRepeat(tag: String) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment: Fragment? = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            fragmentTransaction.remove(fragment)
            fragmentTransaction.commit()
        }
    }

    /**
     * Adding a new snippet, either when clicking on the item navigation bottom bar, or when clicking on the movie
     * @param tag of fragment which need to add
     */
    private fun addFragment(tag: String) {
        checkFragmentRepeat(tag)
        val fragment: Fragment = when (tag) {
            MAIN_FRAGMENT_TAG -> {
                MainFragment.newInstance(screenWidth)
            }
            MOVIE_DETAIL_FRAGMENT_TAG -> {
                if (currentMovieTitle != null) {
                    MovieDetailsFragment.newInstance(currentMovieTitle
                            ?: throw IllegalArgumentException("Recycler required"))
                }
                else{
                    MainFragment.newInstance(screenWidth)
                }
            }
            PROFILE_FRAGMENT_TAG -> {
                ProfileFragment()
            }
            else -> {
                MainFragment()
            }
        }
        supportFragmentManager.beginTransaction()
                .add(R.id.clFragmentContainer, fragment, tag)
                .commit()
    }

    /**
     * keep user genre selection when flipping screen
     * */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FRAGMENT_KEY, currentFragment)
        outState.putString(CURRENT_MOVIE_KEY, currentMovieTitle)
    }

    /**
     * Adding a movie detail fragment when clicking on an element
     * @param title of the movie the details of which want to show
     */
    override fun onMovieItemClicked(title: String) {
        currentMovieTitle = title
        addFragment(MOVIE_DETAIL_FRAGMENT_TAG)
    }


}



