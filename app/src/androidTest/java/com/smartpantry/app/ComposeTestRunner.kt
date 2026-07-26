package com.smartpantry.app

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner

class ComposeTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String, context: Context): Application {
        return super.newApplication(cl, className, context)
    }

    override fun onCreate(arguments: Bundle) {
        // Disable default activity launch for Compose tests
        // Compose tests use createComposeRule() which doesn't need an Activity
        super.onCreate(arguments)
    }

    override fun getTargetContext(): Context {
        return super.getTargetContext()
    }
}