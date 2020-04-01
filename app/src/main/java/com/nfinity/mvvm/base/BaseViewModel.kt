package com.nfinity.mvvm.base

import androidx.lifecycle.ViewModel
import com.nfinity.mvvm.di.component.DaggerViewModelInjector
import com.nfinity.mvvm.di.component.ViewModelInjector
import com.nfinity.mvvm.di.module.NetworkModule
import com.nfinity.mvvm.ui.post.PostListViewModel
import com.nfinity.mvvm.ui.post.PostViewModel


abstract class BaseViewModel: ViewModel(){
    private val injector: ViewModelInjector = DaggerViewModelInjector
            .builder()
            .networkModule(NetworkModule)
            .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is PostListViewModel -> injector.inject(this)
            is PostViewModel -> injector.inject(this)
        }
    }
}