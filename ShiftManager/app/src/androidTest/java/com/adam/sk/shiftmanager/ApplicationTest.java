package com.adam.sk.shiftmanager;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    @SmallTest
    public void smallTest(){
        assertEquals(4,4);
    }

//    @Test
//    public void first(){
//        assertEquals(4,4);
//    }
}