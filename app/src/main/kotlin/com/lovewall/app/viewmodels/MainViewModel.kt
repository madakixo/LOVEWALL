package com.lovewall.app.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {
    fun pairUser(inviteCode: String) {
        // Logic to pair via Firestore invite doc
    }

    // Handle onboarding logic
}
