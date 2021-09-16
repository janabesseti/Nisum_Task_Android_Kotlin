package com.noogler.nisum_task_android_kotlin.justretrofit

import com.noogler.nisum_task_android_kotlin.datamodel.Sf
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RetroService {

    @GET("dictionary.py")
    fun getWordsSFApi(@Query("sf") query : String ) : Observable<Sf>

    @GET("dictionary.py")
    fun getWordsLfApi(@Query("lf") query : String ) : Observable<Sf>

    @GET("/posts")
    fun getWordsListFromApiMock(@Query("sf") query : String ): Single<Sf>
}