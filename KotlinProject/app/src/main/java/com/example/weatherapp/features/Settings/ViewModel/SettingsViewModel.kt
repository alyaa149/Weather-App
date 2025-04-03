package com.example.weatherapp.features.Settings.ViewModel

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Utils.AppContext
import com.example.weatherapp.Utils.constants.AppStrings
import com.example.weatherapp.Utils.sharedprefrences.sharedPreferencesUtils
import com.example.weatherapp.data.repo.RepoImpl
import com.example.weatherapp.features.alerts.viewmodel.AlertViewModel
import com.google.android.gms.common.util.SharedPreferencesUtils

class SettingsViewModel: ViewModel() {
    var selectedLanguage = mutableStateOf(sharedPreferencesUtils.getData(AppStrings().LANGUAGEKEY) ?: "en")


}

class SettingsViewModelFactory() : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel() as T
    }
}