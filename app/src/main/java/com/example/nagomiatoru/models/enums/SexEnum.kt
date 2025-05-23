package com.example.nagomiatoru.models.enums

enum class SexEnum(val value: String, val displayName: String) {
    F("F", "Female"),
    M("M", "Male"),
    O("O", "Other"),
    N("N", "Prefer not to say");

    override fun toString(): String {
        return displayName
    }

    companion object {
        fun fromValue(value: String): SexEnum? {
            return values().find { it.value == value }
        }

        fun getDisplayList(): List<String> {
            return values().map { it.displayName }
        }
    }
}
