package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_getReminderById() = runTest {
        val reminder = ReminderDTO(
            "Title", "Description", "Location", 19.8086935, 105.7086535
        )
        repository.saveReminder(reminder)

        val result = repository.getReminder(reminder.id)
        result as Result.Success
        assertThat(result.data.id, `is`(reminder.id))
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))

    }

    @Test
    fun saveReminder_deleteAllReminders() = runTest {
        val reminder1 = ReminderDTO(
            "Title 1", "Description 1", "Location 1", 10.0, 10.0
        )
        val reminder2 = ReminderDTO(
            "Title 2", "Description 2", "Location 2", 20.0, 20.0
        )
        val reminder3 = ReminderDTO(
            "Title 3", "Description 3", "Location 3", 30.0, 30.0
        )
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)

        repository.deleteAllReminders()

        val result = repository.getReminders() as Result.Success
        assertThat(result.data.isEmpty(), `is`(true))
    }

    @Test
    fun getReminderById_notExitReminder() = runTest {
        val result = repository.getReminder("Fake ID") as Result.Error

        assertThat(result, `is`(true))
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun saveReminder_getReminders() = runTest {
        val reminder1 = ReminderDTO(
            "Title 1", "Description 1", "Location 1", 10.0, 10.0
        )
        val reminder2 = ReminderDTO(
            "Title 2", "Description 2", "Location 2", 20.0, 20.0
        )
        val reminder3 = ReminderDTO(
            "Title 3", "Description 3", "Location 3", 30.0, 30.0
        )

        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)

        val result = repository.getReminders() as Result.Success
        assertThat(result.data.size, `is`(3))
    }
}
