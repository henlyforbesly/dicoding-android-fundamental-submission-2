package com.example.submission.ui.main

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.submission.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun assertGetFavoriteUser() {
        onView(withId(R.id.recyclerViewUsers)).perform(RecyclerViewActions.actionOnItem<ListUserAdapter.ViewHolder>(hasDescendant(withText("jim")), click()))
        onView(withId(R.id.floatingActionButtonIsFavorite)).perform(click())
        pressBack()
        onView(withId(R.id.favorite)).perform(click())
        onView(withId(R.id.recyclerViewUsers)).check(matches(hasDescendant(withText("jim"))))
    }
}
