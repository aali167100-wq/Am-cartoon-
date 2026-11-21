package com.example.ui.screens

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

suspend fun signInWithGoogleCredential(
    context: Context,
    auth: FirebaseAuth
): Result<String> {
    return try {
        val webClientId = context.getString(com.example.R.string.default_web_client_id)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        if (credential is androidx.credentials.CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleCredential = GoogleAuthProvider.getCredential(
                googleIdTokenCredential.idToken, null
            )
            val authResult = auth.signInWithCredential(googleCredential).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("User UID is null")
            Result.success(uid)
        } else {
            Result.failure(IllegalStateException("Unexpected credential type"))
        }
    } catch (e: GetCredentialCancellationException) {
        Log.w("Auth", "User cancelled credential picker", e)
        Result.failure(e)
    } catch (e: Exception) {
        Log.e("Auth", "Google sign in failed", e)
        Result.failure(e)
    }
}
