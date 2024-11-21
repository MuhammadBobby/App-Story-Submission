package com.dicoding.picodiploma.loginwithanimation.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailStoryActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
            storyDateOverlay.text = listStoryItem.createdAt?.let { formatDate(it) }

            Glide.with(itemView.context)
                .load(listStoryItem.photoUrl)
                .into(storyImage)

            //click listener
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_ID_STORY, listStoryItem.id)

                //animation shared
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(storyImage, "photo"),
                        Pair(storyName, "name"),
                        Pair(storyDescription, "description"),
                        Pair(storyDateOverlay, "date")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }


        //format date
        private fun formatDate(isoString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Atur timezone UTC

            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()) // Format output: 8 January 2022

            val date = inputFormat.parse(isoString)
            return outputFormat.format(date ?: Date()) // Mengembalikan hasil yang terformat
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
