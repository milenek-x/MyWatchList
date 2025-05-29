package com.milenekx.mywatchlist.data.model

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("iso_3166_1")
    val isoCode: String,
    @SerializedName("english_name")
    val englishName: String
)
