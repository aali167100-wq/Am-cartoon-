package com.example.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class CartoonProject(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "Untitled Cartoon",
    val description: String = "Created with AM CARTOON Studio",
    val fps: Int = 24,
    val totalFrames: Int = 24,
    val characterId: String = "",
    val backgroundId: String = "",
    val updatedAt: Date? = null
)

@IgnoreExtraProperties
data class CharacterModel(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "Hero Toon",
    val headShape: String = "Round",
    val hairStyle: String = "Spiky",
    val outfitStyle: String = "Hero Suit",
    val primaryColorHex: String = "#00E5FF",
    val secondaryColorHex: String = "#B388FF",
    val backstory: String = "An adventurous cartoon hero ready for action.",
    @ServerTimestamp val createdAt: Date? = null
)

@IgnoreExtraProperties
data class BackgroundModel(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "Cyber City",
    val themeType: String = "City",
    val colorGradientStart: String = "#0D0D11",
    val colorGradientEnd: String = "#2E1A47",
    val hasStars: Boolean = true
)

@IgnoreExtraProperties
data class AnimationKeyframe(
    val frameIndex: Int = 0,
    val posX: Float = 0f,
    val posY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f
)

@IgnoreExtraProperties
data class VoiceLineItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val projectId: String = "",
    val characterName: String = "Narrator",
    val textScript: String = "Hello world! Welcome to AM CARTOON.",
    val emotion: String = "Excited",
    val pitch: Float = 1.0f
)
