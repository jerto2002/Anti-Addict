package com.example.appduration.TestFirebase


import com.example.appduration.View.App

data class Backup(
    var userid: String? = null,
    var BatterySaveMode: Boolean? = null,
    var BatterySaveModePercent: Float? = null,
    var UseTime: Float? = null,
    var Applist: ArrayList<App>? = ArrayList<App>(1)
    )