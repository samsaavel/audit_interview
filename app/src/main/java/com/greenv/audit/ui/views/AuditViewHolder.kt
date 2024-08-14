package com.greenv.audit.ui.views

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.greenv.audit.data.Audit
import com.greenv.audit.data.AuditFromDB
import com.greenv.audit.databinding.ListRowBinding
import java.time.LocalDate

class AuditViewHolder(private val binding: ListRowBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindModelToView(audit: AuditFromDB) {
        Log.d("*****AuditListVH", "Binding audit:$audit")
        binding.type.text = audit.TIPO_AUDI
        binding.auditTitle.text = audit.auditoria
        binding.sucursal.text = audit.sucursal
        binding.stage.text = audit.etapa
        binding.dateStart.text = audit.fecha_inicio_real
    }
}