package com.example.makeitso.screens.user_info

import androidx.compose.runtime.mutableStateOf
import com.example.makeitso.model.User
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    //private val accountService: AccountService,
    logService: LogService,
    private val storageService: StorageService,
) : MakeItSoViewModel(logService) {
    val user = mutableStateOf(User())

    init {
        launchCatching {
            storageService.user.collect{
                user.value = user.value.copy(name = it.name)
                user.value = user.value.copy(birthDate = it.birthDate)
                user.value = user.value.copy(userId = it.userId)
                user.value = user.value.copy(isAnonymous = it.isAnonymous)
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
            if (User.userId.isBlank()) {
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