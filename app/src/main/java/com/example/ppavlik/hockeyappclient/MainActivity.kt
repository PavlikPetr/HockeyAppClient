package com.example.ppavlik.hockeyappclient

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.ppavlik.hockeyappclient.data.AppVersion
import com.example.ppavlik.hockeyappclient.databinding.ActivityMainBinding
import com.example.ppavlik.hockeyappclient.requests.getVersionsObservable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private val mViewModel by lazy { MainActivityViewModule(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModule = mViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.release()
    }
}