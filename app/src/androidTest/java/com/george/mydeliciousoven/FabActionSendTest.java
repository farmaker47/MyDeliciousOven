package com.george.mydeliciousoven;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import butterknife.BindString;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

//This test veryfies the existance of Fab button and that intent has specific title to send

@RunWith(AndroidJUnit4.class)
public class FabActionSendTest {

    private static final String tsekare = "Send to:";

    @Rule
    public IntentsTestRule<MainActivity> mActivityIntentRule = new IntentsTestRule<>(
            MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void testExistanceOfFabAndIntentSent(){

        onView(withId(R.id.fab)).check(matches((isDisplayed())));
        onView(withId(R.id.fab)).perform(click());

        intended(allOf(
                hasAction(Intent.ACTION_CHOOSER),
                hasExtra(Intent.EXTRA_TITLE, tsekare)));
    }
}
