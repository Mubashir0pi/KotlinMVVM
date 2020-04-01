package com.nfinity.mvvm.ui.post

import androidx.lifecycle.MutableLiveData
import com.nfinity.mvvm.base.BaseViewModel
import com.nfinity.mvvm.data.model.PostResponseData

class PostViewModel: BaseViewModel() {

    private val name = MutableLiveData<String>()
    private val photo = MutableLiveData<String>()

    fun bind(post: PostResponseData.Datum){

        name.value = post.name
        photo.value = post.photo.toString()
    }


    fun getName():MutableLiveData<String>{
        return name
    }
    fun getIsPhoto():MutableLiveData<String>{
        return photo
    }
}