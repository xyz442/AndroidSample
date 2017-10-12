package cz.androidsample;

import android.graphics.Rect;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Rect rect1=new Rect(0,302,720,1426);
        Rect rect2=new Rect(300,0,600,300);
        boolean intersects = rect1.intersects(rect2.left, rect2.top, rect2.right, rect2.bottom);
        assertTrue(!intersects);
    }
}