package com.noogler.nisum_task_android_kotlin.dynamicinjection


import com.noogler.nisum_task_android_kotlin.common.RetroRxModule
import com.noogler.nisum_task_android_kotlin.viewmodel.MainActivityViewModel
import com.noogler.nisum_task_android_kotlin.viewmodelfactory.WordViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetroRxModule::class])
interface RetroRxComponent {
    fun inject(retroRXViewModel: MainActivityViewModel)
    fun inject(retroViewModelFactory: WordViewModelFactory)
}