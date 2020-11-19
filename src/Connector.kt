package com.example

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.url

object Connector {
    private val githubUsernameRegex = "^[a-zA-Z\\d](?:[a-zA-Z\\d]|-(?=[a-zA-Z\\d])){0,38}\$".toRegex()
    private val numOfContributionsRegex = "(?<=:)\\d+".toRegex()
    private val monsterIndexRegex = "(?<=\"index\":\")[A-Za-z -]+(?=\")".toRegex()

    private const val pointsPerContribution = 0.01
    private val listOfChallengeRating =
        listOf<Double>(
            0.0, 0.125, 0.25, 0.5, 1.0, 2.0, 3.0, 4.0, 5.0,
            6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 15.0,
            16.0, 17.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 30.0
        )

    suspend fun getTotalContributions(nickname: String): Int {
        require(nickname.matches(githubUsernameRegex)) { "Wrong github nickname format" }

        val response = HttpClient().use { client ->
            client.get<String> {
                url(
                    "https://github-contributions-api.herokuapp.com/" +
                            nickname +
                            "/count"
                )
            }
        }

        return numOfContributionsRegex.findAll(response)
            .filter { it.value != "0" }
            .map { it.value.toInt() }
            .toList()
            .sum()
    }

    private fun List<Double>.closestBeneath(value: Double): Double =
        this.filter { value >= it }.max() ?: throw IllegalArgumentException("List is empty")

    suspend fun getMonster(totalContributions: Int): Monster {
        val challengeRating = listOfChallengeRating
            .closestBeneath(totalContributions * pointsPerContribution)

        HttpClient() {
            install(JsonFeature) { serializer = JacksonSerializer() }
        }.use { client ->
            val response = client.get<String> {
                url(
                    "https://www.dnd5eapi.co/api/monsters?" +
                            "challenge_rating=" + challengeRating.toString()
                )
            }

            val monsterIndex = monsterIndexRegex
                .findAll(response)
                .map { it.value }
                .toList()
                .random()

            return client.get<Monster> {
                url(
                    "https://www.dnd5eapi.co/api/monsters/" +
                            monsterIndex
                )
            }
        }
    }
}