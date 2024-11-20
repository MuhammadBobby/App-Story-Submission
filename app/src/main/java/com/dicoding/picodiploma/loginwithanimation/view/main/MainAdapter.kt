package com.dicoding.picodiploma.loginwithanimation.view.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem

class MainAdapter() :
    RecyclerView.Adapter<MainAdapter.StoryViewHolder>() {

    private val stories = mutableListOf<ListStoryItem>()

    class StoryViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        private val storyImage: ImageView = view.findViewById(R.id.iv_item_photo)
        private val storyName: TextView = view.findViewById(R.id.tv_item_name)
        private val storyDescription: TextView = view.findViewById(R.id.tv_item_description)
        private val storyDateOverlay: TextView = view.findViewById(R.id.tv_item_date)

        //bind data ke item
        fun bind(listStoryItem: ListStoryItem) {
            storyName.text = listStoryItem.name
            storyDescription.text = listStoryItem.description
            storyDateOverlay.text = listStoryItem.createdAt

            Glide.with(itemView.context)
                .load(listStoryItem.photoUrl)
                .into(storyImage)

            //click listener
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Item clicked: ${listStoryItem.id}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }


    override fun getItemCount(): Int = stories.size


    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }


    //set data from activity
    @SuppressLint("NotifyDataSetChanged")
    fun setDataStories(data: List<ListStoryItem>) {
        stories.clear()
        stories.addAll(data)
        notifyDataSetChanged()
    }
}
