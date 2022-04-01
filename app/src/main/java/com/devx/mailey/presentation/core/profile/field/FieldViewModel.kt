package com.devx.mailey.presentation.core.profile.field

import androidx.lifecycle.ViewModel
import com.devx.mailey.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FieldViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :
    ViewModel() {

    fun changeField(name: String, value: String) {
        databaseRepository.changeUserField(name, value)
    }

}