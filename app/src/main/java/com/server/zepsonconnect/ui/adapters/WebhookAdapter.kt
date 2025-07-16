package com.server.zepsonconnect.ui.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.server.zepsonconnect.R
import com.server.zepsonconnect.databinding.ItemWebhookBinding
import com.server.zepsonconnect.modules.webhooks.domain.WebHookDTO

class WebhookAdapter : ListAdapter<WebHookDTO, WebhookAdapter.ViewHolder>(WebhookDiffCallback()) {
    class ViewHolder(private val binding: ItemWebhookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(webhook: WebHookDTO) {
            binding.apply {
                idText.text = binding.root.context.getString(R.string.webhook_id_format, webhook.id)
                urlText.text = webhook.url
                eventText.text = webhook.event.value
                sourceText.text = when (webhook.source) {
                    com.server.zepsonconnect.domain.EntitySource.Local -> binding.root.context.getString(
                        R.string.local
                    )

                    com.server.zepsonconnect.domain.EntitySource.Gateway,
                    com.server.zepsonconnect.domain.EntitySource.Cloud -> binding.root.context.getString(
                        R.string.cloud
                    )
                }
                sourceIcon.setImageResource(
                    when (webhook.source) {
                        com.server.zepsonconnect.domain.EntitySource.Local -> R.drawable.ic_local_server
                        com.server.zepsonconnect.domain.EntitySource.Cloud,
                        com.server.zepsonconnect.domain.EntitySource.Gateway -> R.drawable.ic_cloud_server
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWebhookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val holder = ViewHolder(binding)

        binding.root.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val webhook = getItem(position)
                val context = binding.root.context
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Webhook ID", webhook.id)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, R.string.id_copied, Toast.LENGTH_SHORT).show()
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WebhookDiffCallback : DiffUtil.ItemCallback<WebHookDTO>() {
        override fun areItemsTheSame(oldItem: WebHookDTO, newItem: WebHookDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WebHookDTO, newItem: WebHookDTO): Boolean {
            return oldItem == newItem
        }
    }
}
