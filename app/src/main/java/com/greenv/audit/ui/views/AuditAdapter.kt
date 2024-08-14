package com.greenv.audit.ui.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenv.audit.data.AuditFromDB
import com.greenv.audit.databinding.ListRowBinding

class AuditAdapter(private var audits: List<AuditFromDB>) :
    RecyclerView.Adapter<AuditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditViewHolder {
        val binding = ListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditViewHolder(binding)
    }

    override fun getItemCount(): Int = audits.size

    override fun onBindViewHolder(holder: AuditViewHolder, position: Int) {
        holder.bindModelToView(audits[position])
    }
}