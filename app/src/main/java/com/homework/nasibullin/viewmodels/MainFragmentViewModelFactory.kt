package com.homework.nasibullin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homework.nasibullin.repo.TestGetMovieListData

class MainFragmentViewModelFactory (private val testGetMovieListData: TestGetMovieListData): ViewModelProvider.Factory  {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MainFragmentViewModel::class.java)) {
            return MainFragmentViewModel(TestGetMovieListData()) as T
        }
        throw IllegalArgumentException("Unknown class name")

    }

}