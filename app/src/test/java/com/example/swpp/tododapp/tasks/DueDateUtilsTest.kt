package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DueDateUtilsTest {

    @Test
    fun dueDateLabel_completedWithoutDueDate_returnsClear() {
        // Given
        val today = LocalDate.parse("2026-10-20")

        // When
        val label = dueDateLabel(null, today, true)

        // Then
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_completedOverdueTask_returnsClear() {
        // Given
        val today = LocalDate.parse("2026-10-20")
        val dueDate = LocalDate.parse("2026-10-19")

        // When
        val label = dueDateLabel(dueDate, today, true)

        // Then
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmpty() {
        // Given
        val today = LocalDate.parse("2026-10-20")

        // When
        val label = dueDateLabel(null, today, false)

        // Then
        assertEquals("", label)
    }

    @Test
    fun dueDateLabel_dueToday_returnsDDay() {
        // Given
        val today = LocalDate.parse("2026-10-20")

        // When
        val label = dueDateLabel(today, today, false)

        // Then
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_dueInThreeDays_returnsDMinusThree() {
        // Given
        val today = LocalDate.parse("2026-10-20")
        val dueDate = LocalDate.parse("2026-10-23")

        // When
        val label = dueDateLabel(dueDate, today, false)

        // Then
        assertEquals("D-3", label)
    }

    @Test
    fun dueDateLabel_overdueByThreeDays_returnsDPlusThree() {
        // Given
        val today = LocalDate.parse("2026-10-20")
        val dueDate = LocalDate.parse("2026-10-17")

        // When
        val label = dueDateLabel(dueDate, today, false)

        // Then
        assertEquals("D+3", label)
    }

    @Test
    fun dueDateLabel_datesAroundToday_returnsAdjacentLabels() {
        // Given
        val today = LocalDate.parse("2026-10-20")
        val dueDates = listOf("2026-10-19", "2026-10-20", "2026-10-21")
            .map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, false) }

        // Then
        assertEquals(listOf("D+1", "D-Day", "D-1"), labels)
    }

    @Test
    fun dueDateLabel_futureDatesAroundMonthBoundary_returnsTotalDays() {
        // Given
        val today = LocalDate.parse("2026-10-20")
        val dueDates = listOf("2026-11-19", "2026-11-20", "2026-11-21")
            .map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, false) }

        // Then
        assertEquals(listOf("D-30", "D-31", "D-32"), labels)
    }

    @Test
    fun dueDateLabel_pastDatesAroundMonthBoundary_returnsTotalDays() {
        // Given
        val today = LocalDate.parse("2026-10-20")
        val dueDates = listOf("2026-09-21", "2026-09-20", "2026-09-19")
            .map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, false) }

        // Then
        assertEquals(listOf("D+29", "D+30", "D+31"), labels)
    }

    @Test
    fun dueDateLabel_datesAroundYearEnd_returnsCalendarDayCounts() {
        // Given
        val today = LocalDate.parse("2026-12-30")
        val dueDates = listOf("2026-12-31", "2027-01-01", "2027-01-02")
            .map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, false) }

        // Then
        assertEquals(listOf("D-1", "D-2", "D-3"), labels)
    }

    @Test
    fun dueDateLabel_datesAroundLeapDay_returnsCalendarDayCounts() {
        // Given
        val today = LocalDate.parse("2024-02-28")
        val dueDates = listOf("2024-02-28", "2024-02-29", "2024-03-01")
            .map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, false) }

        // Then
        assertEquals(listOf("D-Day", "D-1", "D-2"), labels)
    }
}
