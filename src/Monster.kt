package com.example

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Monster(
    val name: String,
    @JsonAlias("challenge_rating")
    val challengeRating: Int,
    val size: String,
    val type: String,
    val alignment: String,
    @JsonAlias("armor_class")
    val armorClass: Int,
    @JsonAlias("hit_points")
    val hitPoints: Int,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int
)
