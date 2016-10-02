package com.example.ppavlik.hockeyappclient.data

import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class CrachesPerDayTypeAdapter : JsonDeserializer<CrachesPerDay>, JsonSerializer<CrachesPerDay> {
    companion object {
        const val DATE_PATTERN = "yyyy-MM-dd"
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CrachesPerDay {
        var date: Date? = null
        var count: Int = 0
        json?.let { array ->
            if (array is JsonArray) {
                val jsonDate = array.get(0)
                val jsonCount = array.get(1)
                date = SimpleDateFormat(DATE_PATTERN).parse(jsonDate.asString)
                count = jsonCount.asInt
            }
        }
        return CrachesPerDay(date, count)
    }

    override fun serialize(src: CrachesPerDay?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonArray {
        val array = JsonArray()
        array.add(SimpleDateFormat(DATE_PATTERN).format(src?.date))
        array.add(src?.count)
        return array
    }
}