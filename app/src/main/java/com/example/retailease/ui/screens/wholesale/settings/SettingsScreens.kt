package com.example.retailease.ui.screens.wholesale.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retailease.ui.uiModels.SettingItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    padding: PaddingValues,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val actualDbFileName = "RetailEase.db"

    // Collect states
    val backupState by settingsViewModel.backupState.collectAsStateWithLifecycle()
    val restoreState by settingsViewModel.restoreState.collectAsStateWithLifecycle()
    val printMode by settingsViewModel.printMode.collectAsStateWithLifecycle()
    val openCashDrawer by settingsViewModel.openCashDrawer.collectAsStateWithLifecycle()

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            settingsViewModel.performBackup(uri, actualDbFileName)
        } else {
            Toast.makeText(context, "Backup location not selected", Toast.LENGTH_SHORT).show()
        }
    }

    val createRestoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            settingsViewModel.performRestore(uri, actualDbFileName)
        } else {
            Toast.makeText(context, "Restore file not selected", Toast.LENGTH_SHORT).show()
        }
    }

    val createJsonExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) {uri: Uri? ->
        if (uri != null){
            settingsViewModel.performJsonExport(uri)
        }
        else{
            Toast.makeText(context, "Json file not selected", Toast.LENGTH_SHORT).show()
        }
    }

    val createJsonImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {uri: Uri? ->
        if(uri != null){
            settingsViewModel.restoreJsonExport(uri)
        }
        else{
            Toast.makeText(context, "Json file not selected", Toast.LENGTH_SHORT).show()
        }
    }
    // Handle backup state changes
    LaunchedEffect(backupState) {
        when (backupState) {
            is BackupState.Success -> {
                Toast.makeText(context, (backupState as BackupState.Success).message, Toast.LENGTH_LONG).show()
                settingsViewModel.clearBackupState()
            }
            is BackupState.Failure -> {
                Toast.makeText(context, "Backup failed: ${(backupState as BackupState.Failure).message}", Toast.LENGTH_LONG).show()
                settingsViewModel.clearBackupState()
            }
            else -> { /* No action needed for Idle and Loading */ }
        }
    }

    // Handle restore state changes
    LaunchedEffect(restoreState) {
        when (restoreState) {
            is RestoreState.Success -> {
                Toast.makeText(context, (restoreState as RestoreState.Success).message, Toast.LENGTH_LONG).show()
                settingsViewModel.clearRestoreState()
            }
            is RestoreState.Failure -> {
                Toast.makeText(context, "Restore failed: ${(restoreState as RestoreState.Failure).message}", Toast.LENGTH_LONG).show()
                settingsViewModel.clearRestoreState()
            }
            else -> { /* No action needed for Idle and Loading */ }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(padding)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val items = listOf(
            SettingItem(
                "Backup",
                "Exports entire database including orders, products and customers.",
                Icons.Default.Save
            ),
            SettingItem(
                "Restore",
                "Restore & Overrides your entire database including orders, products and customers.",
                Icons.Default.Restore
            ),
            SettingItem(
                "Export to Json",
                "Export products and salesman to a JSON file.",
                Icons.Default.Code
            ) ,
            SettingItem(
                "Import from Json",
                "Import and override products and salesman from a JSON file.",
                Icons.Default.FileDownload
            ),
            SettingItem(
                "Print Category Wise",
                "This option will print all the namkeen in category wise",
                Icons.Default.LocalPrintshop
            ),
            SettingItem(
                "Open Cash Drawer",
                "Open Cash drawer after printing.",
                Icons.Default.CurrencyExchange
            )
        )

        items.forEach { item ->
            SettingOption(
                item = item,
                isLoading = when (item.title) {
                    "Backup" -> backupState is BackupState.Loading
                    "Restore" -> restoreState is RestoreState.Loading
                    else -> false
                },
                switchValue = when(item.title){
                    "Print Category Wise" -> printMode
                    "Open Cash Drawer" -> openCashDrawer
                    else -> false
                },
                onSwitchClick = {checked ->
                    when(item.title){
                        "Print Category Wise" -> settingsViewModel.changePrintMode(checked)
                        "Open Cash Drawer" -> settingsViewModel.changeOpenCashDrawer(checked)
                    }
                },
                onClick = {
                    when (item.title) {
                        "Backup" -> {
                            if (backupState !is BackupState.Loading) {
                                val date = Date()
                                val format = SimpleDateFormat("ddMMyy_HHmm", Locale.getDefault())
                                val formattedDate = format.format(date)
                                createBackupLauncher.launch("retail_backup${formattedDate}.db")
                            }
                        }
                        "Restore" -> {
                            if (restoreState !is RestoreState.Loading) {
                                createRestoreLauncher.launch(arrayOf("application/octet-stream"))
                            }
                        }
                        "Export to Json" ->{
                            val date = Date()
                            val format = SimpleDateFormat("ddMMyy_HHmm", Locale.getDefault())
                            val formattedDate = format.format(date)
                            createJsonExportLauncher.launch("retail_Json${formattedDate}.json")
                        }
                        "Import from Json" ->{
                            createJsonImportLauncher.launch(arrayOf("application/json"))
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SettingOption(
    item: SettingItem,
    isLoading: Boolean = false,
    switchValue: Boolean,
    onSwitchClick: (Boolean) -> Unit,
    onClick: () -> Unit = {}
) {

    val isSwitchItem = item.title == "Print Category Wise" || item.title == "Open Cash Drawer"


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (!isSwitchItem) it.clickable(enabled = !isLoading) { onClick() } else it
            }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isLoading) Color.Gray else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isLoading) {
                    when (item.title) {
                        "Backup" -> "Creating backup..."
                        "Restore" -> "Restoring database..."
                        else -> item.description
                    }
                } else {
                    item.description
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (isLoading) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isSwitchItem) {
            Switch(
                checked = switchValue,
                onCheckedChange = { checked ->
                    onSwitchClick(checked)
                }
            )
        } else {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
