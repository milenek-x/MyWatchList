package com.milenekx.mywatchlist.data.model

import com.google.gson.annotations.SerializedName

data class SpokenLanguage(
    @SerializedName("iso_639_1")
    val isoCode: String,
    val name: String,
    @SerializedName("english_name")
    val englishName: String
)