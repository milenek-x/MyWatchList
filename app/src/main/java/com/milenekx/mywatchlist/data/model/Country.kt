package com.milenekx.mywatchlist.data.model

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("iso_3166_1") // Use @SerializedName if your JSON key differs from Kotlin property name
    val iso_3166_1: String, // e.g., "US", "GB", "IN"
    @SerializedName("english_name")
    val english_name: String
    // There might be other fields like native_name, but these are essential
)
