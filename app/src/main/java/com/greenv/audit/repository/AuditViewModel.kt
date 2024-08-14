package com.greenv.audit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenv.audit.database.DBHelper
import com.greenv.audit.network.NetworkLibrary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuditViewModel(context: Context) : ViewModel() {

    private val auditRepository: AuditRepositoryContract =
        AuditRepository(
            auditApi = NetworkLibrary().auditApi,
            dbHelper = DBHelper(context)
        )
    private val _auditState: MutableStateFlow<AuditState> = MutableStateFlow(AuditState.None)
    val auditState: StateFlow<AuditState> by lazy { _auditState }

    fun handleIntent(intent: AuditIntent) {
        when (intent) {
            AuditIntent.getAuditsIntent -> handleFetchAudits()
        }
    }

    private fun handleFetchAudits() {
        _auditState.value = AuditState.Loading
        viewModelScope.launch {
//            auditRepository.getAudits()
            auditRepository.getDataFromDB().observeForever { audits ->
                _auditState.value = if (audits.isNullOrEmpty()) {
                    Log.d("*****VModel", "data from DB is Null")
                    AuditState.Failure
                } else {
                    Log.d("*****VModel", "data from DB is NOT null")
                    AuditState.Success(audits)
                }
            }
//                { audits->
//
//                    NetworkResponse.Failure -> AuditState.Failure
//                    is NetworkResponse.Success -> AuditState.Success(response.data)
//                }
        }
    }
}