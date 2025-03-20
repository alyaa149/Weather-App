package com.example.weatherapp.Favorites.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Home.ViewModel.HomeViewModel
import com.example.weatherapp.Utils.Location.LocationRepository
import com.example.weatherapp.data.repo.RepoImpl

class FavViewModel(private val weatherRepository: RepoImpl) : ViewModel() {

}
class FavViewModelFactory(private val repo: RepoImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavViewModel(repo) as T
    }
}