package com.example.appduration

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getAppname
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

private val applicationInfo: ApplicationInfo = ApplicationInfo()


class GetAppNameTest {
    @Mock
    private lateinit var packageManager: PackageManager

    @Mock
    private lateinit var packageManager2: PackageManager


    private var packagenaam = "com.duration.appname";

    @Test
    fun testGetCorrectName() {
        applicationInfo.name = "test"
        applicationInfo.packageName = packagenaam
        packageManager = mock<PackageManager>(){
            on{
                getApplicationInfo("com.duration.appname", 0)
            }doReturn (applicationInfo)
            on{
                getApplicationLabel(applicationInfo)
            }doReturn (applicationInfo.name)
        }

        var name = getAppname("com.duration.appname", packageManager);
        Assert.assertEquals(name, "test")
    }

    @Test
    fun testwhenNonExistingPackageName() {
        applicationInfo.name = "test"
        applicationInfo.packageName = packagenaam
        packageManager = mock<PackageManager>(){
            on{
                getApplicationInfo("com.duration.appname", 0)
            }doReturn (applicationInfo)
            on{
                getApplicationLabel(applicationInfo)
            }doReturn (applicationInfo.name)
        }

        var name = getAppname("com.duration.appnam", packageManager);
        Assert.assertEquals(name, "")
    }
}
