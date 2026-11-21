package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.example.R
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartoonRepository(context: Context) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    private val aiModel by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.8-flash")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getProjects(userId: String): List<CartoonProject> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("users").document(userId)
                .collection("projects").get().await()
            snapshot.toObjects(CartoonProject::class.java)
        } catch (e: Exception) {
            Log.e("Repo", "Error fetching projects", e)
            emptyList()
        }
    }

    suspend fun saveProject(userId: String, project: CartoonProject): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (project.id.isBlank()) {
                db.collection("users").document(userId).collection("projects").document()
            } else {
                db.collection("users").document(userId).collection("projects").document(project.id)
            }
            val data = mapOf(
                "id" to docRef.id,
                "userId" to userId,
                "title" to project.title,
                "description" to project.description,
                "fps" to project.fps,
                "totalFrames" to project.totalFrames,
                "characterId" to project.characterId,
                "backgroundId" to project.backgroundId,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repo", "Error saving project", e)
            Result.failure(e)
        }
    }

    suspend fun getCharacters(userId: String): List<CharacterModel> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("users").document(userId)
                .collection("characters").get().await()
            snapshot.toObjects(CharacterModel::class.java)
        } catch (e: Exception) {
            Log.e("Repo", "Error fetching characters", e)
            emptyList()
        }
    }

    suspend fun saveCharacter(userId: String, character: CharacterModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (character.id.isBlank()) {
                db.collection("users").document(userId).collection("characters").document()
            } else {
                db.collection("users").document(userId).collection("characters").document(character.id)
            }
            val data = mapOf(
                "id" to docRef.id,
                "userId" to userId,
                "name" to character.name,
                "headShape" to character.headShape,
                "hairStyle" to character.hairStyle,
                "outfitStyle" to character.outfitStyle,
                "primaryColorHex" to character.primaryColorHex,
                "secondaryColorHex" to character.secondaryColorHex,
                "backstory" to character.backstory,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repo", "Error saving character", e)
            Result.failure(e)
        }
    }

    suspend fun generateAiCharacter(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val model = aiModel ?: return@withContext "Energetic cartoon hero ready for action."
            val response = model.generateContent("Create a fun cartoon character profile with name, traits, and backstory based on this idea: $prompt.")
            response.text ?: "Energetic cartoon star ready for adventure."
        } catch (e: Exception) {
            Log.e("Repo", "AI generation failed", e)
            "A charismatic cartoon hero with limitless energy."
        }
    }

    suspend fun generateAiVoiceScript(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val model = aiModel ?: return@withContext "Let's go on an epic adventure right now!"
            val response = model.generateContent("Write a short snappy 2-sentence cartoon dialogue line based on: $prompt.")
            response.text ?: "Let's go on an epic adventure right now!"
        } catch (e: Exception) {
            Log.e("Repo", "AI voice script failed", e)
            "Woohoo! Time to animate our world!"
        }
    }
}
