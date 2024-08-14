package com.greenv.audit.network

import com.greenv.audit.data.AuditResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuditApi {

    @GET("ServicioADA_Test/AdaDataService.svc/ObtenerMisAuditoriasPorFecha")
    suspend fun getAuditsByDate(
        @Query("LlaveMaestra") value: String,
    ): AuditResponse
}
