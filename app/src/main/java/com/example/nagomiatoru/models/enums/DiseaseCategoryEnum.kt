package com.example.nagomiatoru.models.enums

enum class DiseaseCategoryEnum(val displayName: String) {
    CARDIOVASCULAR("Cardiovascular"),
    RESPIRATORY("Respiratory"),
    ENDOCRINE("Endocrine"),
    NEUROLOGICAL("Neurological"),
    MENTAL_HEALTH("Mental Health"),
    AUTOIMMUNE("Autoimmune"),
    INFECTIOUS("Infectious"),
    CANCER("Cancer"),
    DIGESTIVE("Digestive"),
    MUSCULOSKELETAL("Musculoskeletal"),
    METABOLIC("Metabolic"),
    OTHER("Other");

    override fun toString(): String = displayName
}
