package com.server.zepsonconnect.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DividerItemDecoration
import com.server.zepsonconnect.R
import com.server.zepsonconnect.data.entities.Message
import com.server.zepsonconnect.databinding.FragmentMessagesListBinding
import com.server.zepsonconnect.modules.messages.vm.MessagesListViewModel
import com.server.zepsonconnect.ui.adapters.MessagesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MessagesListFragment : Fragment(), MessagesAdapter.OnItemClickListener<Message> {

    private val viewModel: MessagesListViewModel by viewModel()
    private val messagesAdapter = MessagesAdapter(this)
    private var _binding: FragmentMessagesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.messages.observe(this) {
            messagesAdapter.submitList(it) {
                _binding?.recyclerView?.scrollToPosition(0)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMessagesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = messagesAdapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() =
            MessagesListFragment()
    }

    override fun onItemClick(item: Message) {
        parentFragmentManager.commit {
            replace(R.id.rootLayout, MessageDetailsFragment.newInstance(item.id))
            addToBackStack(null)
        }
    }
}