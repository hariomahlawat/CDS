package com.concepts_and_quizzes.cds.ui.reports

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportsSharedViewModelTest {
    @Test
    fun persistsWindowSelection() {
        val handle = SavedStateHandle()
        val vm = ReportsSharedViewModel(handle)
        assertEquals(Window.D7, vm.window.value)

        vm.setWindow(Window.D30)
        assertEquals(Window.D30, vm.window.value)

        val vm2 = ReportsSharedViewModel(handle)
        assertEquals(Window.D30, vm2.window.value)
    }
}
