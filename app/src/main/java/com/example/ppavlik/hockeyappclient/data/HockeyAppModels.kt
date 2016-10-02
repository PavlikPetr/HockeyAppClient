package com.example.ppavlik.hockeyappclient.data

import java.util.*
import kotlin.comparisons.compareValuesBy

data class AppVersion(val version: String, val shortversion: String, val title: String,
                      val timestamp: Long, val appsize: Long, val mandatory: Boolean, val external: Boolean,
                      val id: Int, val app_id: Long, val config_url: String, val restricted_to_tags: Boolean,
                      val status: Int, val created_at: String, val updated_at: String, val block_crashes: Boolean,
                      val app_owner: String)

data class Versions(val app_versions: List<AppVersion>)

data class CrachesPerDay(val date: Date?, val count: Int)

data class Histogram(val histogram: List<CrachesPerDay>)

data class HistogramWithVersion(val version: AppVersion, val histogram: Histogram)