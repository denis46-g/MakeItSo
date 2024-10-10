/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.screens.settings

import com.example.makeitso.LOGIN_SCREEN
import com.example.makeitso.SETTINGS_SCREEN
import com.example.makeitso.SIGN_UP_SCREEN
import com.example.makeitso.SPLASH_SCREEN
import com.example.makeitso.TASKS_SCREEN
import com.example.makeitso.USER_INFO_SCREEN
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map

// может, надо поменять import credentials
/*import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID*/

@HiltViewModel
class SettingsViewModel @Inject constructor(
  logService: LogService,
  private val accountService: AccountService,
  private val storageService: StorageService
) : MakeItSoViewModel(logService) {
  val uiState = accountService.currentUser.map {
    SettingsUiState(it.isAnonymous)
  }

  fun onLoginClick(openScreen: (String) -> Unit) = openScreen(LOGIN_SCREEN)

  /*suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> {
    val firebaseAuth = FirebaseAuth.getInstance()
    return callbackFlow {
      try {
        // Initialize Credential Manager
        val credentialManager: CredentialManager = CredentialManager.create(context)

        // Generate a nonce (a random number used once)
        val ranNonce: String = UUID.randomUUID().toString()
        val bytes: ByteArray = ranNonce.toByteArray()
        val md: MessageDigest = MessageDigest.getInstance("SHA-1")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }
        // Set up Google ID option
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
          .setFilterByAuthorizedAccounts(false)
          .setServerClientId("45271224150-5ctki83u25n2ohuq5c8m4q4vigpvdrdc.apps.googleusercontent.com")
          .setNonce(hashedNonce)
          .build()
        // Request credentials
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
          .addCredentialOption(googleIdOption)
          .build()
        // Get the credential result
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        // Check if the received credential is a valid Google ID Token
        if (credential is CustomCredential && credential.type ==
          GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdTokenCredential =
            GoogleIdTokenCredential.createFrom(credential.data)
          val authCredential =
            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
          val authResult = firebaseAuth.signInWithCredential(authCredential).await()
          trySend(Result.success(authResult))
        } else {
          throw RuntimeException("Received an invalid credential type")
        }
      } catch (e: GetCredentialCancellationException) {
        trySend(Result.failure(Exception("Sign-in was canceled. Please try again.")))
      } catch (e: Exception) {
        trySend(Result.failure(e))
      }
      awaitClose { }
    }
  }*/

  fun onSignUpClick(openScreen: (String) -> Unit) = openScreen(SIGN_UP_SCREEN)

  fun onUserInfoClick(openScreen: (String) -> Unit) = openScreen(USER_INFO_SCREEN)

  fun onSignOutClick(restartApp: (String) -> Unit) {
    launchCatching {
      accountService.signOut()
      restartApp(SPLASH_SCREEN)
    }
  }

  fun onDeleteMyAccountClick(restartApp: (String) -> Unit) {
    launchCatching {
      storageService.delete_user(accountService.currentUserId)
      accountService.deleteAccount()
      restartApp(SPLASH_SCREEN)
    }
  }
}
