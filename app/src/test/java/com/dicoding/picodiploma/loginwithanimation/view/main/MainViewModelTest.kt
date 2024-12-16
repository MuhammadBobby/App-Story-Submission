package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.DummyDataTesting
import com.dicoding.picodiploma.loginwithanimation.ListMainDispatchRule
import com.dicoding.picodiploma.loginwithanimation.data.repositories.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.repositories.UserRepository
import com.dicoding.picodiploma.loginwithanimation.getOrAwaitValue
import com.dicoding.picodiploma.loginwithanimation.services.responses.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    //rule & mock
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = ListMainDispatchRule()

    @Mock
    private lateinit var storyRepository: StoriesRepository

    @Mock
    private lateinit var userRepository: UserRepository

    //init mock
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }


    //POSITIVE CASE
    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val dummyStories = DummyDataTesting.generateDummyQuoteResponse()
        val data: PagingData<ListStoryItem> = StoriesPagingSource.snapshot(dummyStories) //move data to paging source
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getListStoriesPaging()).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(userRepository, storyRepository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.pagingStories.getOrAwaitValue()

        //move paging data to adapter
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapterPaging.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        //check result
        Assert.assertNotNull(differ.snapshot()) //testing data not null
        Assert.assertEquals(dummyStories.size, differ.snapshot().size) //testing size
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0]) //testing first data
    }


    //NEGATIVE CASE
    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getListStoriesPaging()).thenReturn(expectedQuote)
        val mainViewModel = MainViewModel(userRepository, storyRepository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.pagingStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapterPaging.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}


val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}


//get dummy data
class StoriesPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}