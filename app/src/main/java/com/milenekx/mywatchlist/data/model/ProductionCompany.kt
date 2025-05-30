package com.milenekx.mywatchlist.data.model

import com.google.gson.annotations.SerializedName

data class ProductionCompany(
    val id: Int,
    val name: String,
    @SerializedName("logo_path")
    val logoPath: String?,
    @SerializedName("origin_country")
    val originCountry: String
)