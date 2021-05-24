package site.antontikhonov.android.domain.notification

import java.util.*

class CalendarRepositoryImpl : CalendarRepository {
    private var calendar = Calendar.getInstance()

    override fun getMutableUserCalendar(): Calendar = calendar

    override fun getNow(): Calendar = Calendar.getInstance()

    override fun setDate(year: Int, month: Int, day: Int) {
        with(calendar) {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
    }
}