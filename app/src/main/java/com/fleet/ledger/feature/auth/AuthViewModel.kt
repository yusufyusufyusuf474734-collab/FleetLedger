package com.fleet.ledger.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.auth.AuthManager
import com.fleet.ledger.core.auth.AuthState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authManager: AuthManager
) : ViewModel() {
    
    val authState: StateFlow<AuthState> = authManager.authState
    val currentUser = authManager.currentUser
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authManager.signIn(email, password)
        }
    }
    
    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            authManager.signUp(email, password, displayName)
        }
    }
    
    fun signOut() {
        authManager.signOut()
    }
    
    fun signInWithBiometric() {
        // TODO: Biyometrik giriş implementasyonu
    }
}
