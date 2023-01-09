package com.example.appduration

import android.app.usage.UsageEvents
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getTotalTimeApps
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate
import java.time.ZoneId


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

private val data: HashMap<String, ArrayList<UsageEvents.Event>> =  HashMap<String, ArrayList<UsageEvents.Event>>();


class GetTimeAppTest {
    @Mock
    private lateinit var mockUsageEvents: UsageEvents.Event

    @Mock
    private lateinit var mockUsageEvents2: UsageEvents.Event

    @Test
    fun testTotalTime() {
        mockUsageEvents = mock<UsageEvents.Event>(){
            on{
                eventType
            } doReturn ( UsageEvents.Event.ACTIVITY_RESUMED) // ACTIVITY_RESUMED = 1, ACTIVITY_PAUSED = 2

            on{
                timeStamp
            } doReturn(1000)

        }
        data.put("app1", arrayListOf(mockUsageEvents, mockUsageEvents))
        var result = getTotalTimeApps(data, start);
        var to = (result.values.sum() / 60000).toFloat()
    }

    @Test
    fun testTotalTime2() {
        mockUsageEvents = mock<UsageEvents.Event>(){
            on{
                eventType
            } doReturn ( UsageEvents.Event.ACTIVITY_RESUMED) // ACTIVITY_RESUMED = 1, ACTIVITY_PAUSED = 2

            on{
                timeStamp
            } doReturn(0)
        }
        mockUsageEvents2 = mock<UsageEvents.Event>(){
            on{
                eventType
            } doReturn ( UsageEvents.Event.ACTIVITY_PAUSED) // ACTIVITY_RESUMED = 1, ACTIVITY_PAUSED = 2

            on{
                timeStamp
            } doReturn(60000)
        }
        data.put("app1", arrayListOf(mockUsageEvents, mockUsageEvents2))
        val currentTime = System.currentTimeMillis()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        var result = getTotalTimeApps(data, start);
        var to = (result.values.sum() / 60000).toFloat()
        assertEquals(to, 1F)
        //var event = UsageEvents.Event();
        //data.put("app1", )
    }
}