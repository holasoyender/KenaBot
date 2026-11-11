/*
 * File:          SkipVote.kt
 * Path:          v3\src\main\kotlin\functions\SkipVote.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 36b025732222985ee3a2113f85a90bbc994beac5885931186026bb58b4ba16ec
 *
 */


package functions

import org.json.JSONArray
import org.json.JSONObject

object SkipVote {

    private val allVotes: MutableList<JSONObject> = ArrayList()
    /*
    {
        id: La ID del servidor
        votes: Array de las IDs de usuarios que han votado
        createdAt: Fecha de creación del objeto
        allowedUsers: Array de IDs de usuarios que pueden votar
    }
    */

    val getVoteById: (id: String) -> JSONObject? = { id ->
        allVotes.firstOrNull { it.getString("id") == id }
    }

    val createVote: (id: String, allowedUsers: List<String>) -> JSONObject = { id, allowedUsers ->
        val vote = JSONObject()
        vote.put("id", id)
        vote.put("votes", JSONArray())
        vote.put("createdAt", System.currentTimeMillis())
        vote.put("allowedUsers", JSONArray(allowedUsers))
        allVotes.add(vote)
        vote
    }

    val addVote: (id: String, userId: String) -> JSONObject = { id, userId ->
        val vote = getVoteById(id) ?: throw Exception("El voto no existe")
        val votes = vote.getJSONArray("votes")
        votes.put(userId)
        vote.put("votes", votes)
        vote
    }

    val userVoted: (id: String, userId: String) -> Boolean = { id, userId ->
        val vote = getVoteById(id) ?: throw Exception("El voto no existe")
        val votes = vote.getJSONArray("votes")
        votes.toList().contains(userId)
    }

    val canVote: (id: String, userId: String) -> Boolean = { id, userId ->
        val vote = getVoteById(id) ?: throw Exception("El voto no existe")
        val allowedUsers = vote.getJSONArray("allowedUsers")
        allowedUsers.toList().contains(userId)
    }

    val getPercentage: (id: String) -> Int = { id ->
        val vote = getVoteById(id) ?: throw Exception("El voto no existe")
        val total = vote.getJSONArray("allowedUsers").length()
        val votes = vote.getJSONArray("votes")
        val percentage = (votes.length() * 100) / total
        percentage
    }

    val endVote: (id: String) -> Boolean = { id ->
        val vote = getVoteById(id) ?: throw Exception("El voto no existe")
        allVotes.remove(vote)
    }
}