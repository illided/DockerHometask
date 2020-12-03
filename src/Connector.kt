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
        this::class.java.getResource("/challenge-rating-list.txt")
            .readText()
            .split("\n")
            .map { it.toDouble() }

    suspend fun getTotalContributions(nickname: String): Int {
        // Check the nickname before setting it in the url
        require(nickname.matches(githubUsernameRegex)) { "Wrong github nickname format" }

        // Getting from github-contributions-api calendar of contributions for the last year:
        //{
        // "data": {
        //      <year1>: {
        //          <month1>: {
        //              <day1>:<numOfContributions>,
        //              <day2>:...,
        //              ...
        //          },
        //          <month2>: {
        //              <day1>:<numOfContributions>,
        //              <day2>:...,
        //              ...
        //          },
        //          ...
        //      },
        //      <year2>: {
        //          ...
        val response = HttpClient().use { client ->
            client.get<String>("https://github-contributions-api.herokuapp.com/$nickname/count")
        }

        return numOfContributionsRegex.findAll(response) // Parsing all num of contributions from calendar
            .filter { it.value != "0" } // Removing all 0
            .map { it.value.toInt() } // Mapping to List<Int>
            .toList()
            .sum() // Summing all contributions
    }

    private fun List<Double>.closestBeneath(value: Double): Double =
        this.filter { value >= it }.max() ?: throw IllegalArgumentException("List is empty")

    suspend fun getMonster(totalContributions: Int): Monster {
        // User gets 0.01 point to his challenge rating per contribution
        // Then result gets rounded down to closest challenge rating
        // from the challengeRatingList
        val challengeRating = listOfChallengeRating
            .closestBeneath(totalContributions * pointsPerContribution)

        HttpClient() {
            install(JsonFeature) { serializer = JacksonSerializer() }
        }.use { client ->
            // Getting Json list of all monsters with given challenge rating
            val response = client.get<String> {
                url(
                    "https://www.dnd5eapi.co/api/monsters?" +
                            "challenge_rating=" + challengeRating.toString()
                )
            }

            val monsterIndex = monsterIndexRegex // Parsing Json list to list of monster indexes
                .findAll(response)
                .map { it.value }
                .toList()
                .random() // And getting random monster index

            // Then we use monster index to get all monsters params we need
            // from www.dnd5eapi.co/api
            return client.get("https://www.dnd5eapi.co/api/monsters/$monsterIndex")
        }
    }
}
