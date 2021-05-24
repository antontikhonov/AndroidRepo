package site.antontikhonov.android.domain.notification

import java.util.Calendar

class BirthdayNotificationModel(
    private val birthdayNotificationRepository: BirthdayNotificationRepository,
    private val calendarRepository: CalendarRepository
    ) : BirthdayNotificationInteractor {

    override fun switchBirthdayNotification(
        id: String,
        contactName: String,
        dayOfBirthday: Int,
        monthOfBirthday: Int
    ) {
        if(checkNotification(id)) {
            makeNotification(id, contactName, dayOfBirthday, monthOfBirthday)
        } else {
            birthdayNotificationRepository.removeNotification(id)
        }
    }

    override fun checkNotification(id: String) = birthdayNotificationRepository.checkNotification(id)

    private fun makeNotification(
        id: String,
        contactName: String,
        dayOfBirthday: Int,
        monthOfBirthday: Int
    ) {
        val birthday = checkDate(dayOfBirthday, monthOfBirthday)
        birthdayNotificationRepository.makeNotification(id, contactName, birthday)
    }

    private fun checkDate(dayOfBirthday: Int, monthOfBirthday: Int): Calendar {
        val currentCalendar = calendarRepository.getMutableUserCalendar()
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        var alarmYear = currentCalendar.get(Calendar.YEAR)
        if (currentMonth > monthOfBirthday || (currentMonth == monthOfBirthday
                    && currentCalendar.get(Calendar.DAY_OF_MONTH) >= dayOfBirthday)) {
            alarmYear++
        }
        if (monthOfBirthday == Calendar.FEBRUARY && dayOfBirthday == 29) {
            while (!isLeap(alarmYear)) {
                alarmYear++
            }
        }
        return calendarRepository.getMutableUserCalendar().apply {
            set(Calendar.YEAR, alarmYear)
            set(Calendar.MONTH, monthOfBirthday)
            set(Calendar.DAY_OF_MONTH, dayOfBirthday)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun isLeap(year: Int) = ((year % 4) == 0 && (year % 100) != 0) || (year % 400) == 0
}