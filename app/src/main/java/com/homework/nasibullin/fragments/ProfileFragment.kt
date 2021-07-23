package com.homework.nasibullin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.homework.nasibullin.R
import com.homework.nasibullin.adapters.GenreAdapter
import com.homework.nasibullin.dataclasses.GenreDto
import com.homework.nasibullin.dataclasses.UserDto
import com.homework.nasibullin.datasourceimpl.UserDataSourceImpl
import com.homework.nasibullin.decorations.GenreItemDecoration
import com.homework.nasibullin.interfaces.OnGenreItemClickedCallback
import com.homework.nasibullin.models.UserModel
import general_staffs.ToastWrapper

class ProfileFragment:Fragment(), OnGenreItemClickedCallback {
    private var user: UserDto? = null
    private lateinit var movieGenreRecycler: RecyclerView
    private lateinit var movieGenreAdapter: GenreAdapter
    private lateinit var toastWrapper: ToastWrapper

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setupView()
    }

    private fun initData(){
        user = UserModel(UserDataSourceImpl()).getUser()
    }

    private fun setupView(){
        toastWrapper = ToastWrapper(context)
        view?.findViewById<TextView>(R.id.tvUserName)?.apply {
            text = user?.name
        }
        view?.findViewById<TextView>(R.id.tvUserMail)?.apply {
            text = user?.mail
        }
        view?.findViewById<TextView>(R.id.etUserName)?.apply {
            text = user?.name
        }
        view?.findViewById<TextView>(R.id.etUserMail)?.apply {
            text = user?.mail
        }
        view?.findViewById<TextView>(R.id.etUserNumber)?.apply {
            text = user?.number
        }
        view?.findViewById<TextView>(R.id.etUserPassword)?.apply {
            text = user?.password
        }
        setupGenreRecycleView(user?.interests)
    }

    private fun setupGenreRecycleView(interests: List<GenreDto>?){
        movieGenreRecycler = view?.findViewById(R.id.rvUserGenreList) ?: throw IllegalArgumentException("Recycler required")
        movieGenreAdapter = GenreAdapter()
        movieGenreAdapter.initOnClickInterface(this)
        movieGenreAdapter.submitList(interests)
        val itemDecorator = GenreItemDecoration(leftRight = MainFragment.GENRE_LEFT_RIGHT_OFFSET)
        movieGenreRecycler.addItemDecoration(itemDecorator)
        movieGenreRecycler.adapter = movieGenreAdapter
        movieGenreRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    override fun onGenreClick(title: String) {
            toastWrapper.showToast(title)
    }



}