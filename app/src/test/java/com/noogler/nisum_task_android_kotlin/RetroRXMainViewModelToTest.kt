package com.noogler.nisum_task_android_kotlin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.noogler.nisum_task_android_kotlin.viewmodel.MainActivityViewModel
import com.noogler.nisum_task_android_kotlin.justretrofit.RetroService
import com.noogler.nisum_task_android_kotlin.datamodel.Sf
import com.noogler.nisum_task_android_kotlin.datamodel.SfItem
import io.reactivex.Single
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RetroRXMainViewModelToTest {

    // A JUnit Test Rule that swaps the background executor used by
    // the Architecture Components with a different one which executes each task synchronously.
    // You can use this rule for your host side tests that use Architecture Components.
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    // Test rule for making the RxJava to run synchronously in unit test
    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var apiService: RetroService

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var single: Single<Sf>

    private var loading: Boolean = false

    @Before
    fun setUp() {
        // initialize the ViewModed with a mocked github api
        mainActivityViewModel = MainActivityViewModel(apiService)
    }

    @Test
    fun fetchRetroInfoTest_success() {
        val retroRxModel = SfItem()
        val retroRXModelList = Sf()
        retroRXModelList.add(retroRxModel)
        single = Single.just(retroRXModelList)

        Mockito.`when`(apiService.getWordsListFromApiMock("HMM")).thenReturn(single)

        mainActivityViewModel.getRetroInfo("HMM")
        Assert.assertEquals(1, mainActivityViewModel.wordList.value?.size)
        Assert.assertEquals(loading, mainActivityViewModel.loading.value)
    }

    @Test
    fun fetchRetroEmptyWordTest_success() {
        val retroRxModel = SfItem()
        val retroRXModelList = Sf()
        retroRXModelList.add(retroRxModel)
        single = Single.just(retroRXModelList)

        Mockito.`when`(apiService.getWordsListFromApiMock("")).thenReturn(single)

        mainActivityViewModel.getRetroInfo("")
        Assert.assertEquals(1, mainActivityViewModel.wordList.value?.size)
        Assert.assertEquals(loading, mainActivityViewModel.loading.value)
    }


    @Test
    fun fetchRetroInfoTest_Failure_Scenario() {
        single = Single.error(Throwable())
        Mockito.`when`(apiService.getWordsListFromApiMock("H")).thenReturn(single)
        mainActivityViewModel.getRetroInfo("H")
        Assert.assertEquals(loading, mainActivityViewModel.loading.value)
        //Assert.assertNull(object : Any??)
    }
}