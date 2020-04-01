package com.nfinity.mvvm.api

import com.nfinity.mvvm.data.model.PostResponseData
import io.reactivex.Single
import retrofit2.http.GET


//Author Muhammad Mubashir 10/30/2018


interface ApiInterface {
    @GET("/api/museums/")
    fun fetchAllPosts(): Single<PostResponseData>

}