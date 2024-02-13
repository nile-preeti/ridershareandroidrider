package com.rideshare.app;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Instrumentation home_fragment, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under home_fragment.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("icanstudioz.com.taxiapp", appContext.getPackageName());
    }
}
