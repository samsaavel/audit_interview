package com.greenv.audit.ui.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.greenv.audit.database.DBHelper
import com.greenv.audit.databinding.FragmentAuditBinding
import com.greenv.audit.repository.AuditIntent
import com.greenv.audit.repository.AuditState
import com.greenv.audit.repository.AuditViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AuditViewModelFactory constructor(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuditViewModel::class.java)) {
            return AuditViewModel(this.context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AuditFragment : Fragment() {

    private var _binding: FragmentAuditBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuditViewModel
    private val db by lazy { DBHelper(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAuditBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = AuditViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(AuditViewModel::class.java)
        Log.d("******AuditsFragment", "View created")
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.auditState.collectLatest { state ->
                when (state) {
                    AuditState.None -> {
                        Log.d("******AuditsFragment", "None state")
                        viewModel.handleIntent(AuditIntent.getAuditsIntent)
                    }

                    AuditState.Failure -> {
                        Log.e("******AuditsFragment", "Failed to load data")
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }

                    AuditState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("******AuditsFragment", "loading")
                    }

                    is AuditState.Success -> {
                        Log.d("******AuditsFragment", "Success state initiated")
                        binding.progressBar.visibility = View.GONE
                        val responseFromDB = db.readTable()
                        Log.d("*****AuditsFragment", "$responseFromDB")
                        binding.recyclerView.adapter =
                            AuditAdapter(responseFromDB)
                        Log.d("******AuditsFragment", "observeViewModel, setup adapter ")

                    }
                }
            }
        }
    }
}