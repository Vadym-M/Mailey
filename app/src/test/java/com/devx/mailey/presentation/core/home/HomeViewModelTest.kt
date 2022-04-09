package com.devx.mailey.presentation.core.home


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.devx.mailey.data.model.Message
import com.devx.mailey.data.model.Room
import com.devx.mailey.data.model.User
import com.devx.mailey.data.repository.DatabaseRepository
import com.devx.mailey.domain.data.RoomItem
import com.devx.mailey.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations



@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class HomeViewModelTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var homeViewModel: HomeViewModel
    @Mock
    private lateinit var databaseRepository: DatabaseRepository
    @Rule @JvmField
    val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        MockitoAnnotations.openMocks(this)
        homeViewModel = HomeViewModel(databaseRepository)
    }

    @Test
    fun getAllRooms() {
        runBlocking {
            Mockito.`when`(databaseRepository.getRooms()).thenReturn(
                mutableListOf(
                    Room(
                        mutableMapOf<String, Message>(),
                        "qwewe",
                        "dasd",
                        "asda",
                        "1",
                        "asda"
                    )
                )
            )
            val user1 = User("Test", "ttt", "1", HashMap(), mutableListOf("test"), "ssds", "1234")
            val user2 = User("Test2", "ttt2", "2", HashMap(), mutableListOf("test2"), "ssds2", "12342")
            Mockito.`when`(databaseRepository.getUserById(user2.id)).thenReturn(user2)
            homeViewModel.getUserRooms(user1)
            val result = homeViewModel.rooms.getOrAwaitValue()
            assertEquals(listOf(RoomItem("dasd", "qwewe", " ", "asda", "Empty list", "00:00")), result)
        }
    }
}




