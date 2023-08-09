package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    private lateinit var remindersDao: RemindersDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()
        remindersDao = database.reminderDao()
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
        remindersDao.saveReminder(reminder)
        val loadedReminder = remindersDao.getReminderById(reminder.id)
        assertThat(loadedReminder, `is`(reminder))
    }

    @Test
    fun deleteAllReminders_returnEmptyList() = runTest {
        val reminder1 = ReminderDTO(
            "Title 1", "Description 1", "Location 1", 10.0, 10.0
        )
        val reminder2 = ReminderDTO(
            "Title 2", "Description 2", "Location 2", 20.0, 20.0
        )

        remindersDao.saveReminder(reminder1)
        remindersDao.saveReminder(reminder2)

        remindersDao.deleteAllReminders()

        val remindersSize = remindersDao.getReminders().size
        assertThat(remindersSize, `is`(0))
    }
}
