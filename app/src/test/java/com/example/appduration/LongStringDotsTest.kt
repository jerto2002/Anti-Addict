package com.example.appduration

import android.app.usage.UsageEvents
import com.example.appduration.View.MainActivity
import com.example.testmvc.Controller.MainController
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.mock


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

private val data: HashMap<String, ArrayList<UsageEvents.Event>> =  HashMap<String, ArrayList<UsageEvents.Event>>();


class LongStringDotsTest {

    @Mock
    private lateinit var mockmainActivity: MainActivity

    @Test
    fun LongStringDotsTest() {
        mockmainActivity = mock<MainActivity>(){
        }
        var name = "hottentottententoonstelling";
        var controller = MainController(mockmainActivity);

        var result = controller.showPartOfstringWithDots(1, name)
        Assert.assertEquals(result, "h.." )

        var result2 = controller.showPartOfstringWithDots(2, name)
        Assert.assertEquals(result2, "ho.." )

        var result3 = controller.showPartOfstringWithDots(name.length + 1, name)
        Assert.assertEquals(result3, "hottentottententoonstelling" )
    }


}