package com.smartpantry.inventory.data

import androidx.room.TypeConverter
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status

class Converters {
    @TypeConverter
    fun fromCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun toCategory(category: Category): String = category.name

    @TypeConverter
    fun fromStatus(value: String): Status = Status.valueOf(value)

    @TypeConverter
    fun toStatus(status: Status): String = status.name
}