package com.nyc.javadontlie;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Wayne Kellman on 2/16/18.
 */
@RunWith(AndroidJUnit4.class)
public class MoneyActivityTest {


    @Rule
    public ActivityTestRule<MoneyActivity> mActivityRule = new ActivityTestRule<>(
            MoneyActivity.class);
    @Test
    public void addButtonAddToTotal(){
        onView(withId(R.id.input_amount)).perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.input_enter)).perform(click());
        onView(withId(R.id.money_amount)).check(matches(withText("10")));
    }

    @Test
    public void subtractButtonSubsFromTotal(){

        onView(withId(R.id.output_amount)).perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.output_enter)).perform(click());
        onView(withId(R.id.money_amount)).check(matches(withText("-10")));
    }
}
