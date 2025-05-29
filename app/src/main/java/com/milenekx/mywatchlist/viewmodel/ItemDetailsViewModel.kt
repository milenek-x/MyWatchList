package com.milenekx.mywatchlist.viewmodel  // or your package here

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.milenekx.mywatchlist.data.api.RetrofitInstance
import com.milenekx.mywatchlist.data.model.ItemDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKey = "898d7082467df8e6c1bb0e8146a5f281" // Replace with your actual TMDb API key

    suspend fun getMovieDetails(id: Int): ItemDetailsResponse? = withContext(Dispatchers.IO) {
        val response = RetrofitInstance.api.getMovieDetails(id.toString(), apiKey)
        if (response.isSuccessful) response.body() else null
    }

    suspend fun getTVDetails(id: Int): ItemDetailsResponse? = withContext(Dispatchers.IO) {
        val response = RetrofitInstance.api.getTVDetails(id.toString(), apiKey)
        if (response.isSuccessful) response.body() else null
    }
}
