package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem

object DummyDataTesting {

    //generate dummy data for testing
    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyItem = ListStoryItem(
                photoUrl = "https://example.com/photo_$i.jpg",
                createdAt = "2024-12-16T10:00:00Z",
                name = "Author $i",
                description = "This is a description for story $i.",
                lon = 100.0 + i, // Dummy longitude
                id = "id_$i",
                lat = -6.0 - i  // Dummy latitude
            )
            items.add(storyItem)
        }
        return items
    }
}