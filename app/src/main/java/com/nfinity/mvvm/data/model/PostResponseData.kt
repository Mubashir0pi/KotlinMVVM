package com.nfinity.mvvm.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PostResponseData {

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum {

        @SerializedName("id")
        @Expose
        var id: Int? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("photo")
        @Expose
        var photo: String? = null

    }
}