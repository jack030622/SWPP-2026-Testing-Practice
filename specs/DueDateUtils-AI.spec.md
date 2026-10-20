# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the task-list D-day label from whole calendar days between `today` and `dueDate`. Completion takes precedence over the due date.

**Pre-conditions**
- `today` is the local reference date; `dueDate` may be null.

**Post-conditions**
- Success cases
  - A completed task returns `"CLEAR"`, even with no due date. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-20`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"` (`DueDateUtilsTest.dueDateLabel_completedWithoutDueDate_returnsClear`).
  - A completed task with a due date also returns `"CLEAR"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-20`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"` (`DueDateUtilsTest.dueDateLabel_completedOverdueTask_returnsClear`).
  - An active task without a due date returns `""`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `""` (`DueDateUtilsTest.dueDateLabel_activeWithoutDueDate_returnsEmpty`).
  - An active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-20`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-Day"` (`DueDateUtilsTest.dueDateLabel_dueToday_returnsDDay`).
  - An active task due in N calendar days returns `"D-N"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-23`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-3"` (`DueDateUtilsTest.dueDateLabel_dueInThreeDays_returnsDMinusThree`).
  - An active task overdue by N calendar days returns `"D+N"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-17`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+3"` (`DueDateUtilsTest.dueDateLabel_overdueByThreeDays_returnsDPlusThree`).
- Error cases
  - None. Null due dates and both completion states are valid; `LocalDate` values represent valid dates.
- Edge cases
  - The dates immediately before, at, and after today switch from overdue to due today to upcoming. `[Unit]`
    - Tip: Given `today = 2026-10-20`, `isCompleted = false`, and due dates `2026-10-19`, `2026-10-20`, `2026-10-21` / When `dueDateLabel` is called for each / Then the results are `"D+1"`, `"D-Day"`, `"D-1"`, respectively (`DueDateUtilsTest.dueDateLabel_datesAroundToday_returnsAdjacentLabels`).
  - A month boundary counts whole calendar days on both sides. `[Unit]`
    - Tip: Given `today = 2026-01-30`, `isCompleted = false`, and due dates `2026-01-31`, `2026-02-01`, `2026-02-02` / When `dueDateLabel` is called for each / Then the results are `"D-1"`, `"D-2"`, `"D-3"`, respectively (`DueDateUtilsTest.dueDateLabel_datesAroundMonthEnd_returnsCalendarDayCounts`).
  - A year boundary counts whole calendar days on both sides. `[Unit]`
    - Tip: Given `today = 2026-12-30`, `isCompleted = false`, and due dates `2026-12-31`, `2027-01-01`, `2027-01-02` / When `dueDateLabel` is called for each / Then the results are `"D-1"`, `"D-2"`, `"D-3"`, respectively (`DueDateUtilsTest.dueDateLabel_datesAroundYearEnd_returnsCalendarDayCounts`).
  - A leap day counts as one calendar day, with dates on both sides. `[Unit]`
    - Tip: Given `today = 2024-02-28`, `isCompleted = false`, and due dates `2024-02-28`, `2024-02-29`, `2024-03-01` / When `dueDateLabel` is called for each / Then the results are `"D-Day"`, `"D-1"`, `"D-2"`, respectively (`DueDateUtilsTest.dueDateLabel_datesAroundLeapDay_returnsCalendarDayCounts`).

**Undefined behavior** (optional)
- None.
