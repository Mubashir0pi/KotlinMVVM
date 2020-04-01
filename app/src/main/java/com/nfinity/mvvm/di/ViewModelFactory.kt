package com.nfinity.mvvm.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nfinity.mvvm.data.model.PostResponseData
import com.nfinity.mvvm.ui.post.PostListViewModel


class ViewModelFactory(private val activity: AppCompatActivity): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}