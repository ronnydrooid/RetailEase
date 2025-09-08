// In your settings/SettingsViewModel.kt
package com.example.retailease.ui.screens.wholesale.settings

import android.app.Application // Or androidx.lifecycle.AndroidViewModel
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.retailease.datasource.preferences.PreferencesManager
import com.example.retailease.datasource.room.AppDatabase
import com.example.retailease.repository.ProductRepository
import com.example.retailease.ui.components.JsonBackupManager
import com.example.retailease.ui.components.backupToStorage
import com.example.retailease.ui.components.restoreFromStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appDatabase: AppDatabase, // Inject the Room database
    private val application: Application,
    private val preferencesManager: PreferencesManager,
    private val productRepository: ProductRepository,
    private val jsonBackupManager: JsonBackupManager,
) : ViewModel() {

    val printMode = preferencesManager.printModeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val openCashDrawer = preferencesManager.openCashDrawerFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState = _backupState.asStateFlow()

    private val _restoreState = MutableStateFlow<RestoreState>(RestoreState.Idle)
    val restoreState = _restoreState.asStateFlow()

    fun performJsonExport(uri: Uri) {
        viewModelScope.launch {

            try {

                val backup = jsonBackupManager.createBackupJson()
                jsonBackupManager.saveToStorage(
                    application.applicationContext,
                    destinationUri = uri,
                    backup
                ).fold(
                    onSuccess = { message ->
                        Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                    },
                    onFailure = {
                        Toast.makeText(
                            application.applicationContext,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

            } catch (e: Exception) {

            }
        }
    }

    fun restoreJsonExport(uri: Uri) {
        viewModelScope.launch {
            try {
                jsonBackupManager.restoreFromStorage(application.applicationContext,uri).fold(
                    onSuccess = { message ->
                        Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT)
                            .show()
                    },
                    onFailure = {
                        Toast.makeText(
                            application.applicationContext,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                )
            } catch (e: Exception) {

            }
        }
    }

    fun performBackup(uri: Uri, dbFileName: String = "RetailEase.db") {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading

            try {
                withContext(Dispatchers.IO) {
                    productRepository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
                    delay(100)

                    backupToStorage(application.applicationContext, uri, dbFileName).fold(
                        onSuccess = { message ->
                            _backupState.value = BackupState.Success(message)
                        },
                        onFailure = { exception ->
                            _backupState.value =
                                BackupState.Failure(exception.message ?: "Unknown Error")
                        }

                    )
                }


            } catch (e: Exception) {
                _backupState.value = BackupState.Failure(e.message ?: "Unknown Error")
            }
        }
    }

    fun performRestore(uri: Uri, dbFileName: String = "RetailEase.db") {
        viewModelScope.launch {
            _restoreState.value = RestoreState.Loading

            try {
                withContext(Dispatchers.IO) {

                    appDatabase.close()
                    delay(100)



                    restoreFromStorage(application.applicationContext, uri, dbFileName).fold(
                        onSuccess = { message ->
                            _restoreState.value = RestoreState.Success(message)
                            exitProcess(0)
                        },
                        onFailure = { exception ->
                            _restoreState.value =
                                RestoreState.Failure(exception.message ?: "Unknown Error")
                        }
                    )
                }

            } catch (e: Exception) {
                _restoreState.value = RestoreState.Failure(e.message ?: "Unknown Error")
            }
        }
    }

    fun clearBackupState() {
        _backupState.value = BackupState.Idle
    }

    fun clearRestoreState() {
        _restoreState.value = RestoreState.Idle
    }

    fun changePrintMode(value: Boolean){
        viewModelScope.launch {
            preferencesManager.changePrintMode(value)
        }
    }

    fun changeOpenCashDrawer(value: Boolean){
        viewModelScope.launch {
            preferencesManager.changeOpenCashDrawer(value)
        }
    }
}

sealed class BackupState {
    data object Idle : BackupState()
    data object Loading : BackupState()
    data class Success(val message: String) : BackupState()
    data class Failure(val message: String) : BackupState()
}

sealed class RestoreState {
    data object Idle : RestoreState()
    data object Loading : RestoreState()
    data class Success(val message: String) : RestoreState()
    data class Failure(val message: String) : RestoreState()
}