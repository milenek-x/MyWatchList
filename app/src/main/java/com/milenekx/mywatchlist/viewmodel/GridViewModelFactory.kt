package com.milenekx.mywatchlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.milenekx.mywatchlist.data.repository.Repository

class GridViewModelFactory(private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GridViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GridViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
