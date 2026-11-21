package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CartoonRepository(application)

    private val _projects = MutableStateFlow<List<CartoonProject>>(emptyList())
    val projects: StateFlow<List<CartoonProject>> = _projects.asStateFlow()

    private val _characters = MutableListFlowState(emptyList<CharacterModel>())
    val characters: StateFlow<List<CharacterModel>> = _characters

    private val _currentProject = MutableStateFlow(CartoonProject(title = "My Epic Cartoon", fps = 24, totalFrames = 30))
    val currentProject: StateFlow<CartoonProject> = _currentProject.asStateFlow()

    private val _currentCharacter = MutableStateFlow(CharacterModel(name = "ToonStar", headShape = "Round", hairStyle = "Spiky", outfitStyle = "Hero Suit"))
    val currentCharacter: StateFlow<CharacterModel> = _currentCharacter.asStateFlow()

    private val _currentFrame = MutableStateFlow(1)
    val currentFrame: StateFlow<Int> = _currentFrame.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _stageTransform = MutableStateFlow(AnimationKeyframe(frameIndex = 1, posX = 0f, posY = 0f, scale = 1f, rotation = 0f))
    val stageTransform: StateFlow<AnimationKeyframe> = _stageTransform.asStateFlow()

    private val _aiResultText = MutableStateFlow("")
    val aiResultText: StateFlow<String> = _aiResultText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadUserData(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _projects.value = repository.getProjects(userId)
            val chars = repository.getCharacters(userId)
            if (chars.isNotEmpty()) {
                _characters.value = chars
                _currentCharacter.value = chars.first()
            } else {
                val defaultChar = CharacterModel(userId = userId, name = "ToonStar")
                repository.saveCharacter(userId, defaultChar)
                _characters.value = listOf(defaultChar)
            }
            _isLoading.value = false
        }
    }

    fun updateCurrentProject(project: CartoonProject) {
        _currentProject.value = project
    }

    fun updateCurrentCharacter(character: CharacterModel) {
        _currentCharacter.value = character
    }

    fun saveProject(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            repository.saveProject(userId, _currentProject.value)
            _projects.value = repository.getProjects(userId)
        }
    }

    fun saveCharacter(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            repository.saveCharacter(userId, _currentCharacter.value)
            _characters.value = repository.getCharacters(userId)
        }
    }

    fun generateAiCharacterConcept(prompt: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val backstory = repository.generateAiCharacter(prompt)
            _currentCharacter.value = _currentCharacter.value.copy(backstory = backstory, name = prompt.take(15))
            _isLoading.value = false
        }
    }

    fun generateAiVoice(prompt: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val script = repository.generateAiVoiceScript(prompt)
            _aiResultText.value = script
            _isLoading.value = false
        }
    }

    fun setCurrentFrame(frame: Int) {
        _currentFrame.value = frame.coerceIn(1, _currentProject.value.totalFrames)
    }

    fun togglePlay() {
        _isPlaying.value = !_isPlaying.value
    }

    fun updateTransform(posX: Float, posY: Float, scale: Float, rotation: Float) {
        _stageTransform.value = AnimationKeyframe(
            frameIndex = _currentFrame.value,
            posX = posX,
            posY = posY,
            scale = scale,
            rotation = rotation
        )
    }
}

// Helper for mutable state flow list
private fun <T> MutableListFlowState(initialValue: T) = MutableStateFlow(initialValue)
