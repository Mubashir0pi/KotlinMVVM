package com.nfinity.mvvm.di.component

import com.nfinity.mvvm.di.module.NetworkModule
import com.nfinity.mvvm.ui.post.PostListViewModel
import com.nfinity.mvvm.ui.post.PostViewModel
import dagger.Component

import javax.inject.Singleton

/**
 * Component providing inject() methods for presenters.
 **/

@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector {

    fun inject(postListViewModel: PostListViewModel)

    fun inject(postViewModel: PostViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}