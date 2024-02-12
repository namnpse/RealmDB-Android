package com.namnp.realmandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RealmApp: Application()

// Teacher 1-to-1 Address
// Teacher 1-to-many Course
// Students many-to-many Course