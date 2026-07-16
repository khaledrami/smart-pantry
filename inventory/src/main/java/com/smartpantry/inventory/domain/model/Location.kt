package com.smartpantry.inventory.domain.model

data class Location(val path: String) {
    val levels: List<String>
        get() = path.split("/").filter { it.isNotBlank() }

    val storageType: String?
        get() = levels.firstOrNull()

    val zone: String?
        get() = levels.getOrNull(1)

    val slot: String?
        get() = levels.getOrNull(2)

    override fun toString(): String = path

    companion object {
        val FREEZER = "Freezer"
        val FRIDGE = "Fridge"
        val PANTRY = "Pantry"
    }
}