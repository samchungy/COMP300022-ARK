package ark.ark.Map;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Until;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by SamCh on 8/10/2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapTest {

    @Rule
    public ActivityTestRule<MapNavDrawer> mActivityRule = new ActivityTestRule<>(
            MapNavDrawer.class);

    @Test
    public void marker_click_test(){

    }
}
