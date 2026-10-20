package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    @Test
    fun setDueDateLabel_upcomingTask_showsLabel() {
        // Given
        val today = LocalDate.now()
        val task = Task(isCompleted = false, dueDate = today.plusDays(3))
        val view = textView("old", View.GONE)

        // When
        setDueDateLabel(view, task)

        // Then
        assertLabel(view, "D-3", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_completedTask_showsClear() {
        // Given
        val task = Task(isCompleted = true, dueDate = null)
        val view = textView("old", View.GONE)

        // When
        setDueDateLabel(view, task)

        // Then
        assertLabel(view, "CLEAR", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_completedOverdueTask_showsClear() {
        // Given
        val today = LocalDate.now()
        val task = Task(isCompleted = true, dueDate = today.minusDays(1))
        val view = textView("D+1", View.VISIBLE)

        // When
        setDueDateLabel(view, task)

        // Then
        assertLabel(view, "CLEAR", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_activeTaskWithoutDueDate_hidesLabel() {
        // Given
        val task = Task(isCompleted = false, dueDate = null)
        val view = textView("D-3", View.VISIBLE)

        // When
        setDueDateLabel(view, task)

        // Then
        assertLabel(view, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_nullTask_hidesLabel() {
        // Given
        val view = textView("CLEAR", View.VISIBLE)

        // When
        setDueDateLabel(view, null)

        // Then
        assertLabel(view, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_datesAroundToday_showsAdjacentLabels() {
        // Given
        val today = LocalDate.now()
        val view = textView("old", View.GONE)
        val cases = listOf(
            Task(isCompleted = false, dueDate = today.minusDays(1)) to "D+1",
            Task(isCompleted = false, dueDate = today) to "D-Day",
            Task(isCompleted = false, dueDate = today.plusDays(1)) to "D-1"
        )

        // When / Then
        for ((task, expectedLabel) in cases) {
            setDueDateLabel(view, task)
            assertLabel(view, expectedLabel, View.VISIBLE)
        }
    }

    @Test
    fun setDueDateLabel_checkThenUncheckUndatedTask_clearsStaleLabel() {
        // Given
        val task = Task(isCompleted = false, dueDate = null)
        val view = textView("old", View.VISIBLE)

        // When / Then
        setDueDateLabel(view, task)
        assertLabel(view, "", View.GONE)

        task.isCompleted = true
        setDueDateLabel(view, task)
        assertLabel(view, "CLEAR", View.VISIBLE)

        task.isCompleted = false
        setDueDateLabel(view, task)
        assertLabel(view, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_checkThenUncheckDatedTask_restoresDayLabel() {
        // Given
        val today = LocalDate.now()
        val task = Task(isCompleted = false, dueDate = today.plusDays(1))
        val view = textView("old", View.GONE)

        // When / Then
        setDueDateLabel(view, task)
        assertLabel(view, "D-1", View.VISIBLE)

        task.isCompleted = true
        setDueDateLabel(view, task)
        assertLabel(view, "CLEAR", View.VISIBLE)

        task.isCompleted = false
        setDueDateLabel(view, task)
        assertLabel(view, "D-1", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_reboundAcrossEmptyLabel_updatesView() {
        // Given
        val today = LocalDate.now()
        val datedTask = Task(isCompleted = false, dueDate = today.plusDays(1))
        val undatedTask = Task(isCompleted = false, dueDate = null)
        val view = textView("old", View.GONE)

        // When / Then
        setDueDateLabel(view, datedTask)
        assertLabel(view, "D-1", View.VISIBLE)

        setDueDateLabel(view, undatedTask)
        assertLabel(view, "", View.GONE)

        setDueDateLabel(view, datedTask)
        assertLabel(view, "D-1", View.VISIBLE)
    }

    private fun textView(text: String, visibility: Int) =
        TextView(InstrumentationRegistry.getInstrumentation().targetContext).apply {
            this.text = text
            this.visibility = visibility
        }

    private fun assertLabel(view: TextView, expectedText: String, expectedVisibility: Int) {
        assertEquals(expectedText, view.text.toString())
        assertEquals(expectedVisibility, view.visibility)
    }
}
