package com.noogler.nisum_task_android_kotlin.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.noogler.nisum_task_android_kotlin.dynamicinjection.DaggerRetroRxComponent
import com.noogler.nisum_task_android_kotlin.justretrofit.*
import com.noogler.nisum_task_android_kotlin.datamodel.Sf
import com.noogler.nisum_task_android_kotlin.views.LogToast
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainActivityViewModel(private val apiService : RetroService) : ViewModel() , LifecycleObserver {

    var postLoadError : MutableLiveData<String> = MutableLiveData()
    var loading : MutableLiveData<Boolean> = MutableLiveData()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    // Toast Messages
    var statusMessage = MutableLiveData<LogToast<String>>()
    val message : LiveData<LogToast<String>>
        get() = statusMessage

    var wordList: MutableLiveData<Sf> = MutableLiveData()

    init {
        DaggerRetroRxComponent.create().inject(this)
        loading.value = true
        //   apiService = retrofit.create(APIService::class.java)
    }

    fun getWordListObserver(): MutableLiveData<Sf> {
        return wordList
    }

    // actual app function
    fun makeApiCall(query: String) {
        val retroInstance = RetroFitInstance.getRetroInstance().create(RetroService::class.java)
        val querySplit = query.split(":").toTypedArray()
        if(querySplit[0] == "1")
            retroInstance.getWordsLfApi(querySplit[1])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getWordListObserverRx(this))
        else
            retroInstance.getWordsSFApi(querySplit[1])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getWordListObserverRx(this))
    }

    companion object {
        fun getWordListObserverRx(mainActivityViewModel: MainActivityViewModel): Observer<Sf> {

            return object : Observer<Sf> {
                override fun onComplete() {
                }
                override fun onError(e: Throwable) {
                    mainActivityViewModel.statusMessage.value = LogToast("Data Error!")
                    mainActivityViewModel.wordList.postValue(null)
                }
                override fun onNext(t: Sf) {
                    Log.d("MainActivityViewModel : onNext() :",t.toString())
                    if (t.size != 0) {
                        mainActivityViewModel.statusMessage.value = LogToast("Matched words Loaded!")
                        mainActivityViewModel.wordList.postValue(t)
                    }else
                        mainActivityViewModel.statusMessage.value = LogToast("Matched words not found!")
                }
                override fun onSubscribe(d: Disposable) {
                }

            }
        }
    }

    // test function
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun getRetroInfo(query : String){
        compositeDisposable.add(apiService.getWordsListFromApiMock(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Sf>(){
                override fun onSuccess(t: Sf) {
                    wordList.value  = t
                    loading.value = false
                }
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    postLoadError.value = e.message
                    loading.value = false
//                   onError(e.localizedMessage)
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun onError(message:String){
        println(message)
    }

}