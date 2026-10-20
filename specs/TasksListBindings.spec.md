# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: Show the task's D-day label in the given `TextView`, or hide the view when there is no label. Every binding must replace the previous text and visibility, including after checking or unchecking a task or reusing a list-item view.

**Pre-conditions**
- `textView` is a valid Android `TextView`; `task` may be null.
- The label uses the device's current local calendar date at the time of the call. In date-dependent Tips, `T` is the date read on the device immediately before binding; the concrete examples use `T = 2026-10-20`. Tests must construct due dates relative to their actual `T` so CI runs on later dates remain valid.

**Post-conditions**
- Success cases
  - An active task with a due date displays its label and makes the view `View.VISIBLE`. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), an active task due `T.plusDays(3)` (example `2026-10-23`), and a `TextView` with text `"old"` and visibility `View.GONE` / When `setDueDateLabel` is called / Then text is `"D-3"` and visibility is `View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_upcomingTask_showsLabel`).
  - A completed task displays `"CLEAR"` and makes the view `View.VISIBLE`, even without a due date. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), a completed task with `dueDate = null`, and a `TextView` with text `"old"` and visibility `View.GONE` / When `setDueDateLabel` is called / Then text is `"CLEAR"` and visibility is `View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_completedTask_showsClear`).
  - A completed task with a due date also replaces the previous text with `"CLEAR"` and makes the view `View.VISIBLE`. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), a completed task due `T.minusDays(1)` (example `2026-10-19`), and a `TextView` with text `"D+1"` and visibility `View.VISIBLE` / When `setDueDateLabel` is called / Then text is `"CLEAR"` and visibility is `View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_completedOverdueTask_showsClear`).
  - An active task without a due date clears previous text and hides the view. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), an active task with `dueDate = null`, and a `TextView` with text `"D-3"` and visibility `View.VISIBLE` / When `setDueDateLabel` is called / Then text is `""` and visibility is `View.GONE` (`TasksListBindingsTest.setDueDateLabel_activeTaskWithoutDueDate_hidesLabel`).
  - A null task clears previous text and hides the view. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), `task = null`, and a `TextView` with text `"CLEAR"` and visibility `View.VISIBLE` / When `setDueDateLabel` is called / Then text is `""` and visibility is `View.GONE` (`TasksListBindingsTest.setDueDateLabel_nullTask_hidesLabel`).
- Error cases
  - None. A null task is valid; a valid `TextView` is required by the pre-condition.
- Edge cases
  - Due dates immediately before, on, and after the device date display adjacent labels. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), active tasks due `T.minusDays(1)`, `T`, and `T.plusDays(1)` (examples `2026-10-19`, `2026-10-20`, `2026-10-21`), and a `TextView` initially holding `"old"` with visibility `View.GONE` / When each task is bound in turn / Then the respective text/visibility pairs are `"D+1"`/`View.VISIBLE`, `"D-Day"`/`View.VISIBLE`, and `"D-1"`/`View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_datesAroundToday_showsAdjacentLabels`).
  - Checking and then unchecking a task without a due date changes its label from absent to `"CLEAR"` and back to absent on the same view. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), one task with `dueDate = null` and `isCompleted = false`, and a `TextView` with text `"old"` and visibility `View.VISIBLE` / When the task is bound as active, then with `isCompleted = true`, then with `isCompleted = false` / Then the text/visibility pairs after each call are `""`/`View.GONE`, `"CLEAR"`/`View.VISIBLE`, and `""`/`View.GONE` (`TasksListBindingsTest.setDueDateLabel_checkThenUncheckUndatedTask_clearsStaleLabel`).
  - Checking and then unchecking a dated task changes its label to `"CLEAR"` and restores its D-day label on the same view. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), one task due `T.plusDays(1)` (example `2026-10-21`) with `isCompleted = false`, and a `TextView` with text `"old"` and visibility `View.GONE` / When the task is bound as active, then with `isCompleted = true`, then with `isCompleted = false` / Then the text/visibility pairs after each call are `"D-1"`/`View.VISIBLE`, `"CLEAR"`/`View.VISIBLE`, and `"D-1"`/`View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_checkThenUncheckDatedTask_restoresDayLabel`).
  - Rebinding across the empty-label boundary removes a stale label, and rebinding to a dated task shows the new label. `[Integration]`
    - Tip: Given device date `T` (example `2026-10-20`), an active task due `T.plusDays(1)` (example `2026-10-21`), an active task with `dueDate = null`, and one `TextView` initially holding `"old"` with visibility `View.GONE` / When the dated task, undated task, and dated task are bound in that order / Then the text/visibility pairs after each call are `"D-1"`/`View.VISIBLE`, `""`/`View.GONE`, and `"D-1"`/`View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_reboundAcrossEmptyLabel_updatesView`).

**Undefined behavior** (optional)
- None.
