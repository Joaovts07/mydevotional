package com.example.mydevotional.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.repositorie.UserRepository
import com.example.mydevotional.usecase.GetSelectedTranslationUseCase
import com.example.mydevotional.usecase.SetSelectedTranslationUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val setSelectedTranslationUseCase: SetSelectedTranslationUseCase,
    getSelectedTranslationUseCase: GetSelectedTranslationUseCase,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
    ) : ViewModel() {

    val localUser = userRepository.getUser().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val selectedTranslation: StateFlow<BibleTranslation> = getSelectedTranslationUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BibleTranslation.ALMEIDA
        )


    fun setTranslation(translation: BibleTranslation) {
        viewModelScope.launch {
            setSelectedTranslationUseCase(translation)
            }
    }

    private fun syncUser(uid: String) {
        viewModelScope.launch {
            userRepository.syncUser(uid)
        }
    }

    init {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            syncUser(currentUser.uid)
        }
        userRepository.observeRemoteUser()
    }


}