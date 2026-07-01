package com.todoapp

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class HiltTestRule(private val testInstance: Any) : TestRule {
    private val hiltRule = HiltAndroidRule(testInstance)

    override fun apply(base: Statement, description: Description): Statement {
        return RuleChain.outerRule(hiltRule).apply(base, description)
    }
}
