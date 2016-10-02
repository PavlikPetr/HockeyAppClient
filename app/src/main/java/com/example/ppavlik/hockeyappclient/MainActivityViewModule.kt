package com.example.ppavlik.hockeyappclient

import android.app.Activity
import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.util.Log
import android.view.View
import com.example.ppavlik.hockeyappclient.data.*
import com.example.ppavlik.hockeyappclient.requests.getCrachesObservable
import com.example.ppavlik.hockeyappclient.requests.getVersionsObservable
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ppavlik on 30.09.16.
 */
class MainActivityViewModule(val activity: Activity) : BaseViewModel() {
    val json = ""
    val status: ObservableField<String> = ObservableField("")
    val crashesVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    val crachesEnabled: ObservableBoolean = ObservableBoolean(false)
    private var mSubscriprion: CompositeSubscription = CompositeSubscription()
    private var mList: List<HistogramWithVersion> = listOf()
    private var versions: Versions? = null

    init {

    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val input = activity.getAssets().open("file.json")
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    fun getAppVersions(view: View) {
        val turnsType = object : TypeToken<List<HistogramWithVersion>>() {}.type
        val gson = GsonBuilder()
                .registerTypeAdapter(CrachesPerDay::class.java, CrachesPerDayTypeAdapter())
                .create()
        val list: List<HistogramWithVersion> = gson.fromJson<List<HistogramWithVersion>>(loadJSONFromAsset(), turnsType)
        var result: Set<HistogramWithVersion> = setOf()
        result = result.plus(list)
        val sortedList=result.toList()
        Collections.sort(sortedList,{first:HistogramWithVersion,second:HistogramWithVersion->
            (second.histogram.histogram[1].count-second.histogram.histogram[0].count)-
                    (first.histogram.histogram[1].count-first.histogram.histogram[0].count)
        })
        val res=gson.toJson(sortedList)
//        var map: Map<String, Int> = HashMap<String, Int>()
//        result.forEach { item ->
//            item.histogram.histogram.forEach { data ->
//                val key = SimpleDateFormat(CrachesPerDayTypeAdapter.DATE_PATTERN).format(data.date?.time)
//                val value = if (map.containsKey(key)) map.get(key)?.plus(data.count) else data.count
//                map = map.plus(mapOf(key to value!!))
//            }
//        }
//        Log.e("Result", "craches ${map.toString()}")

//        status.set("Отправляю запрос крашей по всем версиям")
//        crashesVisibility.set(View.INVISIBLE)
//        mSubscriprion.add(
//                getVersionsObservable()
//                        .subscribeOn(Schedulers.newThread())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(object : Subscriber<Versions>() {
//                            override fun onNext(t: Versions?) {
//                                if (t != null) {
//                                    versions = t
//                                    status.set("Список версий получен")
//                                    crashesVisibility.set(View.VISIBLE)
//                                    crachesEnabled.set(true)
//                                } else {
//                                    status.set("Запрос выполнен с ошибкой")
//                                    crashesVisibility.set(View.INVISIBLE)
//                                }
//                            }
//
//                            override fun onCompleted() {
//                                Log.d("COMPLETED", "")
//                            }
//
//                            override fun onError(e: Throwable?) {
//                                status.set("Ошибка при получении списка версий\n${e?.message}")
//                            }
//                        })
//        )
    }

    fun getCraches(view: View) {
        versions?.app_versions?.let { list ->
            crachesEnabled.set(false)
            mSubscriprion.add(Observable.merge(getObservables(list))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<HistogramWithVersion>() {
                        override fun onError(e: Throwable?) {
                            crachesEnabled.set(true)
                        }

                        override fun onCompleted() {
                            crachesEnabled.set(true)
                            status.set("Готова статистика о крашах в ${mList.size} версиях")
                        }

                        override fun onNext(data: HistogramWithVersion?) {
                            if (data != null) {
                                mList = mList.plus(data)
                            }
                        }
                    }))
        }
    }

    fun catcheAll(list: List<HistogramWithVersion>) {
        status.set("Получен ответ.\nВсего версий ${list.size}")
    }

    fun getObservables(data: List<AppVersion>): List<Observable<HistogramWithVersion>> {
        var list = listOf<Observable<HistogramWithVersion>>()
        val start: Int = if (mList.lastIndex < 0) 0 else data.indexOf(data.find { item -> item.id == mList.last().version.id })
        val end: Int = if (data.size < (start.plus(50))) data.size - 1 else start.plus(50)
        if (start == end) {
            status.set("Получили информацию о всех крашах")
        }
        for (i in start..end) {
            list = list.plus(getCrachesObservable(data.get(i).id).map { HistogramWithVersion(data.get(i), it) })
        }
        return list
    }

    override fun release() {
        super.release()
        mSubscriprion.unsubscribe()
    }
}