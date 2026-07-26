package com.smartpantry.inventory.domain.model

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable

@Serializable
enum class Category(@StringRes val labelRes: Int) {
    MEAT(R.string.category_meat),
    FISH(R.string.category_fish),
    VEGETABLES(R.string.category_vegetables),
    FRUITS(R.string.category_fruits),
    DAIRY(R.string.category_dairy),
    FROZEN(R.string.category_frozen),
    BEVERAGES(R.string.category_beverages),
    CANNED(R.string.category_canned),
    LEGUMES(R.string.category_legumes),
    PASTA(R.string.category_pasta),
    RICE(R.string.category_rice),
    SPICES(R.string.category_spices),
    BREAD(R.string.category_bread),
    SAUCES(R.string.category_sauces),
    SNACKS(R.string.category_snacks),
    OTHER(R.string.category_other)
}