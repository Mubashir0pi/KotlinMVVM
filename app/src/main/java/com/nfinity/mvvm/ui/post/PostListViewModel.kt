package com.nfinity.mvvm.ui.post

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.nfinity.mvvm.base.BaseViewModel
import com.nfinity.mvvm.data.model.PostResponseData
import com.nfinity.mvvm.api.ApiInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class PostListViewModel : BaseViewModel() {
    @Inject

    lateinit var apiInterface: ApiInterface

    val postListAdapter: PostListAdapter =
        PostListAdapter()

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    private lateinit var subscription: Disposable

    init {
        loadPosts()
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    @SuppressLint("CheckResult")
    private fun loadPosts() {
// Fetching all posts
        apiInterface.fetchAllPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .doOnSubscribe { onRetrievePostListStart() }
            .doOnTerminate { onRetrievePostListFinish() }
            .subscribeWith(object : DisposableSingleObserver<PostResponseData>() {
                override fun onSuccess(posts: PostResponseData) {
                    onRetrievePostListSuccess(posts.data!!)
                }

                override fun onError(e: Throwable) {
                    onRetrievePostListError("Server not responding")
                }
            })
    }

    private fun onRetrievePostListStart() {
        loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    private fun onRetrievePostListFinish() {
        loadingVisibility.value = View.GONE
    }

    private fun onRetrievePostListSuccess(postList: List<PostResponseData.Datum>) {
        postListAdapter.updatePostList(postList)
    }

    private fun onRetrievePostListError(message: String) {
        errorMessage.value = message
    }
}