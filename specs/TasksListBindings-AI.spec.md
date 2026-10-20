# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: Show the task's D-day label in the given `TextView`, or hide the view when there is no label. Rebinding must replace any previous task's label and visibility.

**Pre-conditions**
- `textView` is a valid Android `TextView`; `task` may be null.
- The label uses the device's current local calendar date at the time of the call. Tips below assume the device date is `2026-10-20`.

**Post-conditions**
- Success cases
  - An active task with a due date displays its label and makes the view `View.VISIBLE`. `[Integration]`
    - Tip: Given device date `2026-10-20`, an active task due `2026-10-23`, and a `TextView` with text `"old"` and visibility `View.GONE` / When `setDueDateLabel` is called / Then text is `"D-3"` and visibility is `View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_upcomingTask_showsLabel`).
  - A completed task displays `"CLEAR"` and makes the view `View.VISIBLE`, even without a due date. `[Integration]`
    - Tip: Given device date `2026-10-20`, a completed task with `dueDate = null`, and a `TextView` with text `"old"` and visibility `View.GONE` / When `setDueDateLabel` is called / Then text is `"CLEAR"` and visibility is `View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_completedTask_showsClear`).
  - An active task without a due date clears previous text and hides the view. `[Integration]`
    - Tip: Given device date `2026-10-20`, an active task with `dueDate = null`, and a `TextView` with text `"D-3"` and visibility `View.VISIBLE` / When `setDueDateLabel` is called / Then text is `""` and visibility is `View.GONE` (`TasksListBindingsTest.setDueDateLabel_activeTaskWithoutDueDate_hidesLabel`).
  - A null task clears previous text and hides the view. `[Integration]`
    - Tip: Given device date `2026-10-20`, `task = null`, and a `TextView` with text `"CLEAR"` and visibility `View.VISIBLE` / When `setDueDateLabel` is called / Then text is `""` and visibility is `View.GONE` (`TasksListBindingsTest.setDueDateLabel_nullTask_hidesLabel`).
- Error cases
  - None. A null task is valid; a valid `TextView` is required by the pre-condition.
- Edge cases
  - Due dates immediately before, on, and after the device date display adjacent labels. `[Integration]`
    - Tip: Given device date `2026-10-20`, active tasks due `2026-10-19`, `2026-10-20`, and `2026-10-21`, and a `TextView` initially holding `"old"` with visibility `View.GONE` / When each task is bound in turn / Then the respective texts are `"D+1"`, `"D-Day"`, and `"D-1"`, with visibility `View.VISIBLE` each time (`TasksListBindingsTest.setDueDateLabel_datesAroundToday_showsAdjacentLabels`).
  - Rebinding across the empty-label boundary removes a stale label, and rebinding to a dated task shows the new label. `[Integration]`
    - Tip: Given device date `2026-10-20`, an active task due `2026-10-21`, an active task with `dueDate = null`, and one `TextView` initially holding `"old"` with visibility `View.GONE` / When the dated task, undated task, and dated task are bound in that order / Then the text and visibility after each call are `"D-1"`/`View.VISIBLE`, `""`/`View.GONE`, and `"D-1"`/`View.VISIBLE` (`TasksListBindingsTest.setDueDateLabel_reboundAcrossEmptyLabel_updatesView`).

**Undefined behavior** (optional)
- None.
