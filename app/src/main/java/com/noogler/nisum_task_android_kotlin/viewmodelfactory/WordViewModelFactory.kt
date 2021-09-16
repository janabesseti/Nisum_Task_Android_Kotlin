package com.noogler.nisum_task_android_kotlin.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noogler.nisum_task_android_kotlin.dynamicinjection.DaggerRetroRxComponent.*
import com.noogler.nisum_task_android_kotlin.justretrofit.RetroService
import com.noogler.nisum_task_android_kotlin.viewmodel.MainActivityViewModel
import retrofit2.Retrofit
import javax.inject.Inject

class WordViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Inject
    lateinit var retrofit: Retrofit
    private lateinit var apiService: RetroService

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        create().inject(this)
        apiService = retrofit.create(RetroService::class.java)

        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}