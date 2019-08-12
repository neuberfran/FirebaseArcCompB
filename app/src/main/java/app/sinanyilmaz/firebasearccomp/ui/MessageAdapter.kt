package app.sinanyilmaz.firebasearccomp.ui

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import com.bumptech.glide.Glide

import app.sinanyilmaz.firebasearccomp.R
import app.sinanyilmaz.firebasearccomp.databinding.MessageItemBinding
import app.sinanyilmaz.firebasearccomp.model.Message


class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    constructor()
    private var mMessageList: List<Message>? = null


    internal fun setMessageList(messageList: List<Message>) {
        mMessageList = messageList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = DataBindingUtil
                .inflate<MessageItemBinding>(LayoutInflater.from(parent.context),
                        R.layout.message_item,
                        parent,
                        false)

        return MessageViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = mMessageList!![position]
        holder.binding.message = mMessageList!![position]
        //holder.binding.name.setText(message.getUserName());
        if (message.photoUrl != null && !message.photoUrl!!.isEmpty())
            Glide.with(holder.binding.photoImageView.context)
                    .load(message.photoUrl)
                    .thumbnail(0.01f)
                    .into(holder.binding.photoImageView)
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return if (mMessageList == null) 0 else mMessageList!!.size
    }

    inner class MessageViewHolder(val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {

        private val TAG = "Adapter"
    }
}
