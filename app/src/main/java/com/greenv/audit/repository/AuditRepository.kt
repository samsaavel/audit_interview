package com.greenv.audit.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.greenv.audit.data.AuditFromDB
import com.greenv.audit.data.AuditResponse
import com.greenv.audit.database.DBHelper
import com.greenv.audit.network.AuditApi
import com.greenv.audit.network.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface AuditRepositoryContract {
    suspend fun getAudits(): NetworkResponse<AuditResponse>
    fun getDataFromDB(): LiveData<List<AuditFromDB>>
}

class AuditRepository(
    val auditApi: AuditApi,
    val dbHelper: DBHelper,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AuditRepositoryContract {


    override suspend fun getAudits(): NetworkResponse<AuditResponse> =
        withContext(dispatcher) {
            try {
                val response = auditApi.getAuditsByDate("PG0XUep8sk7H6Ve4bK63Bg==")
                dbHelper.insertData(response.result)
                Log.d("******Repository", "getAudits successful")
                NetworkResponse.Success(data = response)
//                Log.d("TAG", "getAudits: $response")
            } catch (e: Throwable) {
                Log.d("******Repository", "getAudits failed")
                NetworkResponse.Failure
            }
        }

    override fun getDataFromDB(): LiveData<List<AuditFromDB>> {
        var data = MutableLiveData<List<AuditFromDB>>()
        CoroutineScope(dispatcher).launch {
            getAudits()
            data.postValue(dbHelper.readTable())
        }
        return data
    }
}