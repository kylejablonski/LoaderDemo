package com.example.loader.loaderdemo;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.loader.loaderdemo.activities.ScrollingActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test class for the {@link ScrollingActivity}
 * Created by kyle.jablonski on 11/25/15.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScrollingActivityTest extends ActivityInstrumentationTestCase2<ScrollingActivity> {


    // Constants
    private static final String SHAPE_TEXT = "Decagon";
    private static final String NUM_SIDES_TEXT = "10";

    public ScrollingActivityTest(){
        super(ScrollingActivity.class);
    }

    @Rule
    public ActivityTestRule<ScrollingActivity> mActivityRule = new ActivityTestRule<>(
            ScrollingActivity.class);

    @Test
    public void testLaunchActivityDetail() {

        // Select the Add button
        Espresso.onView(ViewMatchers.withId(R.id.fab))
                .perform(ViewActions.click());

        // Check to make sure a view is visible
        Espresso.onView(ViewMatchers.withId(R.id.til_name))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEnterShape(){
        Espresso.onView(ViewMatchers.withId(R.id.fab))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.et_shape_name))
                .perform(ViewActions.typeText(SHAPE_TEXT));

        Espresso.onView(ViewMatchers.withId(R.id.et_shape_num_sides))
                .perform(ViewActions.typeText(NUM_SIDES_TEXT), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.btn_save)).perform(ViewActions.click());
    }

    @Test
    public void testRemoveShape(){
        Espresso.onView(ViewMatchers.withText(SHAPE_TEXT))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.btn_delete))
                .perform(ViewActions.click());
    }
}