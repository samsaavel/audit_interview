package com.greenv.audit.repository

import com.greenv.audit.data.AuditFromDB

sealed class AuditState() {
    object None : AuditState()
    object Failure : AuditState()
    object Loading : AuditState()

    data class Success(
        val responseFromDB: List<AuditFromDB>,
    ) : AuditState()
}

sealed class AuditIntent() {
    object getAuditsIntent : AuditIntent()
}