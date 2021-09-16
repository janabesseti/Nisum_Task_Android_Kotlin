package com.noogler.nisum_task_android_kotlin.views

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.noogler.nisum_task_android_kotlin.adapter.WordListAdapter
import com.noogler.nisum_task_android_kotlin.networkcheck.NetworkLiveData
import com.noogler.nisum_task_android_kotlin.viewmodel.MainActivityViewModel
import com.noogler.nisum_task_android_kotlin.viewmodelfactory.WordViewModelFactory
import com.noogler.nisum_task_android_kotlin.views.databinding.ActivityMainBinding
import io.reactivex.Observable.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

open class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel : MainActivityViewModel
    private lateinit var wordListAdapter: WordListAdapter

    //network check
    private lateinit var networkLiveData : NetworkLiveData

    //Data Binding
    private lateinit var activityMainBinding: ActivityMainBinding

    private var languages : Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkLiveData = NetworkLiveData(application)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initNetworkObserver()
        initRecyclerView()
        initOptions()
        initSearchBox()
        loadViewModel()
        logInToast()
    }

    private fun initNetworkObserver(){
        networkLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
                    viewModel.statusMessage.value = LogToast("Network Available : Connected")
                    activityMainBinding.searchViewLayoutWithShortForm.visibility = View.VISIBLE
                    activityMainBinding.wordRecycleList.visibility = View.VISIBLE
                }
                false -> {
                    viewModel.statusMessage.value = LogToast("Network Unavailable : Disconnected")
                    activityMainBinding.searchViewLayoutWithShortForm.visibility = View.GONE
                    activityMainBinding.wordRecycleList.visibility = View.GONE
                }
            }
        })
    }

    private fun initRecyclerView(){

        activityMainBinding.wordRecycleList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val decoration = DividerItemDecoration(applicationContext, VERTICAL)
            addItemDecoration(decoration)
            wordListAdapter = WordListAdapter()
            adapter = wordListAdapter
        }
    }

    private fun initOptions(){
        languages = resources.getStringArray(R.array.searchParameters)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            languages as Array<out String>
        )
        aa.setDropDownViewResource(R.layout.spinner_left_aligned)
        with(activityMainBinding.acronymOrInitialism){
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@MainActivity
            gravity = Gravity.CENTER
        }
    }

    @SuppressLint("CheckResult", "NotifyDataSetChanged")
    private fun initSearchBox(){

        activityMainBinding.searchWord.isActivated = true
        activityMainBinding.searchWord.onActionViewExpanded()
        activityMainBinding.searchWord.inputType = InputType.
                                TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        activityMainBinding.shortform = "Short Form: "
        create<String> {

            activityMainBinding.searchWord.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
             androidx.appcompat.widget.SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(p0: String?): Boolean {
                 return false
             }

             override fun onQueryTextChange(searchedWord: String): Boolean {
                 if(!it.isDisposed && parameterType == 1){
                     it.onNext(searchedWord.trim())
                 }
                 else
                     it.onNext(searchedWord)
                 return false
             }
         })
        }.debounce(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d("MainActivity","Search : $it")
                    if(it.isNotEmpty()){
                        if(parameterType == 1 && it.toString().length >= 2)
                            loadApiData(it.toString())
                        else
                            loadApiData(it.toString().trim())
                    }
                    else {
                        wordListAdapter.wordListData.clear()
                        viewModel.statusMessage.value = LogToast("No words found matching!")
                    }
                    activityMainBinding.shortform = "Short Form: $it"
                    wordListAdapter.notifyDataSetChanged()
                },
                {
                    Log.e("MainActivity","Error : $it")
                },
                {
                    Log.d("MainActivity","Complete :")
                }
            )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadViewModel(){
        viewModel = ViewModelProvider(this,
            WordViewModelFactory()).get(MainActivityViewModel::class.java)

        viewModel.getWordListObserver().observe(this, {
            if(it != null){
                wordListAdapter.wordListData = it
                wordListAdapter.notifyDataSetChanged()
            }else{
                viewModel.statusMessage.value = LogToast("Error in fetching data!")
            }
        })
    }

    private fun loadApiData(query: String) {
        viewModel.makeApiCall("$parameterType:$query")
    }

    private fun logInToast(){
        viewModel.message.observe(this, { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private var parameterType : Int = 1
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parameterType = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        LogToast("Nothing selected")
    }
}

