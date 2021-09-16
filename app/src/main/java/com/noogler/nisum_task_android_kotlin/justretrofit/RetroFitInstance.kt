package com.noogler.nisum_task_android_kotlin.justretrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetroFitInstance {

    companion object{

        private const val baseUrl = "http://www.nactem.ac.uk/software/acromine/"

        fun getRetroInstance() : Retrofit{

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

    }

}