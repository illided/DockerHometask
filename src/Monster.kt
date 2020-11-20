package com.example

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Monster(
    val name: String,
    val challenge_rating: Int,
    val size: String,
    val type: String,
    val alignment: String,
    val armor_class: Int,
    val hit_points: Int,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int
)
