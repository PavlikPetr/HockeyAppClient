package com.example.ppavlik.hockeyappclient.requests

import com.example.ppavlik.hockeyappclient.data.CrachesPerDayTypeAdapter
import com.example.ppavlik.hockeyappclient.data.Histogram
import com.example.ppavlik.hockeyappclient.data.Versions
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap
import rx.Observable
import java.text.SimpleDateFormat
import java.util.*

fun getVersionsObservable() = RetrofitInstance.getRetrofit().create(VersionRequest::class.java).setParams()

fun getCrachesObservable(id: Int) = RetrofitInstance.getRetrofit()
        .create(CrachesRequest::class.java).setParams(id, getDatesRange())

private interface VersionRequest {
    @Headers("X-HockeyAppToken:e904cd3b0b8042a6a8f7ae387bed6d57")
    @GET("app_versions")
    fun setParams(): Observable<Versions>
}

private interface CrachesRequest {
    @Headers("X-HockeyAppToken:e904cd3b0b8042a6a8f7ae387bed6d57")
    @GET("app_versions/{version}/crashes/histogram")
    fun setParams(@Path("version") id: Int, @QueryMap params: Map<String, String>): Observable<Histogram>
}

private fun getDatesRange() = mapOf("start_date" to getDate(3), "end_date" to getDate())

private fun getDate(offset: Int = 0): String {
    val calendar = Calendar.getInstance()
    if (offset != 0) {
        calendar.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - offset)
    }

    return SimpleDateFormat(CrachesPerDayTypeAdapter.DATE_PATTERN)
            .format(calendar.time)
}
