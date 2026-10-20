package com.smartbanksearch;
import org.junit.Test;
/** Execute the activity suite in the standard Gradle test job. */
public class ActivityCategoriesTest
{
    @Test public void fullActivityRegression()
    {
        ActivityCategoriesSelfTest.runAll();
    }
}
