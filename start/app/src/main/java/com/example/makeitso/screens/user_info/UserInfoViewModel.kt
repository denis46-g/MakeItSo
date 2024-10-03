package com.example.makeitso.screens.user_info

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.example.makeitso.USER_ID
import com.example.makeitso.common.ext.idFromParameter
import com.example.makeitso.model.User
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    logService: LogService,
    private val storageService: StorageService,
) : MakeItSoViewModel(logService) {
    val user = mutableStateOf(User())

    init {
        val userId = savedStateHandle.get<String>(USER_ID)
        if (userId != null) {
            launchCatching {
                user.value = storageService.getUser(userId.idFromParameter()) ?: User()
            }
        }
    }

    fun onNameChange(newValue: String) {
        user.value = user.value.copy(name = newValue)
    }

    fun onDateBirthChange(newValue: Long) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
        calendar.timeInMillis = newValue
        val newDueDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(calendar.time)
        user.value = user.value.copy(birthDate = newDueDate)
    }

    fun onDoneClick(popUpScreen: () -> Unit) {
        launchCatching {
            val User = user.value
            if (User.id.isBlank()) {
                storageService.save(User)
            } else {
                storageService.update(User)
            }
            popUpScreen()
        }
    }

    companion object {
        private const val UTC = "UTC"
        private const val DATE_FORMAT = "EEE, d MMM yyyy"
    }
}