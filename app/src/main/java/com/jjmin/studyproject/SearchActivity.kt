package com.jjmin.studyproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    var data = ""
    var subject: PublishSubject<String> = PublishSubject.create()
    var baselist = ArrayList<String>()
    lateinit var adapter: LastAdapter
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        (0..10).forEach {
            baselist.add("text$it")
        }

        adapter = LastAdapter()
        searchRecycler.adapter = adapter
        adapter.submitList(baselist)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e("check", "check")
                subject.onNext(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("check", "check1234")
                subject.onNext(newText!!)
                return true
            }
        })

        subject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { searchItem(it) }
            .subscribe { list ->
                adapter.submitList(list)
            }
    }


    fun searchItem(data: String): Observable<ArrayList<String>>? {
        var list = ArrayList<String>()
        if(data.isNotEmpty()) {
            (0 until baselist.size).forEach {
                if (baselist[it].contains(data))
                    list.add(baselist[it])
            }
        }else{
            list = baselist
        }

        return Observable.fromArray(list)
    }
}
