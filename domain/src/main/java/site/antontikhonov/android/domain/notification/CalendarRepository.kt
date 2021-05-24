package site.antontikhonov.android.domain.notification

import java.util.*

interface CalendarRepository {
    fun getMutableUserCalendar(): Calendar
    fun getNow(): Calendar
    fun setDate(year: Int, month: Int, day: Int)
}