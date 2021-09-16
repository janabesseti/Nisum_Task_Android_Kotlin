package com.noogler.nisum_task_android_kotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.noogler.nisum_task_android_kotlin.views.R
import com.noogler.nisum_task_android_kotlin.views.databinding.RecyclerListWordRowBinding
import com.noogler.nisum_task_android_kotlin.datamodel.Lf
import com.noogler.nisum_task_android_kotlin.datamodel.Sf

class WordListAdapter : RecyclerView.Adapter<WordListAdapter.ViewHolder>() {

    var wordListData = Sf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val wordRowBinder: RecyclerListWordRowBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.recycler_list_word_row,
            parent, false
        )
        return ViewHolder(wordRowBinder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        wordListData.let {
            if(it[0].component1().isNotEmpty())
                holder.bind(it[0].component1())
        }
    }

    override fun getItemCount(): Int {
        return if (wordListData.size != 0 && wordListData[0].component1().isNotEmpty())
            wordListData[0].component1().size
        else 0
    }

    class ViewHolder(private val wordRowBinder: RecyclerListWordRowBinding) :
        RecyclerView.ViewHolder(wordRowBinder.root) {

        //@SuppressLint("SetTextI18n")
        fun bind(item: List<Lf>) {
            wordRowBinder.apply {
                wordRowBinder.longForm = "lfs:\n" + "Freq : " + item[0].component1() + "\n" +
                        "lf: " + item[0].component2() + "\n" + "since: " +
                        item[0].component3()

                wordRowBinder.variation = "vars:\n" + "freq : " + item[0].vars[0].component1() +
                        "\n" + "Lf: " + item[0].vars[0].component2() + "\n" +
                        "since: " + item[0].vars[0].component3()
                wordRowBinder.executePendingBindings()
            }
        }
    }
}