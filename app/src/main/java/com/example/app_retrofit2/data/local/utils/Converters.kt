package com.example.app_retrofit2.data.local.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
fun fromImages(images: List<String>?): String {
    return Gson().toJson(images)
}

    @TypeConverter
    fun toImages(imagesString: String): List<String> {
        return Gson().fromJson(
            imagesString,
            object : TypeToken<List<String>>() {}.type
        )
    }
}