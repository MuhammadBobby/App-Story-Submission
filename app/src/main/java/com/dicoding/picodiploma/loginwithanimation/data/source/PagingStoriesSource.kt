package com.dicoding.picodiploma.loginwithanimation.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem

class PagingStoriesSource(private val storiesRepository: StoriesRepository) : PagingSource<Int, ListStoryItem>() {
    private companion object{
        const val PAGE_INDEX = 1
    }


    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


    //load data from api
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: PAGE_INDEX
            val responseData = storiesRepository.getListPaging(null, position, params.loadSize)
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}