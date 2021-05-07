package site.antontikhonov.android.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.domain.contactdetails.ContactDetailsInteractor
import site.antontikhonov.android.domain.notification.BirthdayNotificationInteractor
import site.antontikhonov.android.domain.notification.BirthdayNotificationModel
import site.antontikhonov.android.domain.notification.BirthdayNotificationRepository
import site.antontikhonov.android.domain.notification.CalendarRepository
import site.antontikhonov.android.domain.notification.CalendarRepositoryImpl
import site.antontikhonov.android.presentation.schedulers.TrampolineSchedulerProvider
import site.antontikhonov.android.presentation.viewmodels.ContactDetailsViewModel
import java.util.Calendar

@RunWith(MockitoJUnitRunner::class)
class ContactDetailsViewModelTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var contactDetailsInteractor: ContactDetailsInteractor
    @Mock
    private lateinit var alarmManager: AlarmManager
    @Mock
    private lateinit var pendingIntent: PendingIntent
    private lateinit var interactor: BirthdayNotificationInteractor
    private lateinit var calendarRepository: CalendarRepository
    private lateinit var birthdayNotificationRepository: BirthdayNotificationRepository
    private lateinit var viewModel: ContactDetailsViewModel
    private val contact = ContactDetailsEntity(
        id = "",
        name = "Иван Иванович",
        phoneList = listOf(),
        emailList = listOf(),
        description = "",
        dayOfBirthday = 8,
        monthOfBirthday = 9,
        image = ""
    )

    @Before
    fun before() {
        birthdayNotificationRepository = BirthdayNotificationStub(alarmManager, pendingIntent)
        calendarRepository = CalendarRepositoryImpl()
        interactor = BirthdayNotificationModel(birthdayNotificationRepository, calendarRepository)
        viewModel = ContactDetailsViewModel(
            contactDetailsInteractor,
            interactor,
            TrampolineSchedulerProvider()
        )
    }

    @Test
    fun whenNoNotificationAndSetOnNextYear() {
        calendarRepository.setDate(1999, Calendar.SEPTEMBER, 9)
        viewModel.switchBirthdayNotification(contact)
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2000)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DATE, 8)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(alarmManager).set(AlarmManager.RTC_WAKEUP, nextBirthday.timeInMillis, pendingIntent)
    }

    @Test
    fun whenNoNotificationAndSetOnCurrentYear() {
        calendarRepository.setDate(1999, Calendar.SEPTEMBER, 7)
        viewModel.switchBirthdayNotification(contact)
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 1999)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DATE, 8)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(alarmManager).set(AlarmManager.RTC_WAKEUP, nextBirthday.timeInMillis, pendingIntent)
    }

    @Test
    fun whenHaveNotificationAndRemoveIt() {
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2000)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DATE, 8)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        calendarRepository.setDate(1999, Calendar.SEPTEMBER, 9)
        birthdayNotificationRepository.makeNotification(contact.id, contact.name, nextBirthday)
        viewModel.switchBirthdayNotification(contact)
        verify(alarmManager).cancel(pendingIntent)
    }

    @Test
    fun whenNoNotificationAndNextYearIsLeap() {
        calendarRepository.setDate(1999, Calendar.MARCH, 2)
        viewModel.switchBirthdayNotification(contact.copy(
            name = "Павел Павлович",
            dayOfBirthday = 29,
            monthOfBirthday = 2,
        ))
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2000)
            set(Calendar.MONTH,  Calendar.FEBRUARY)
            set(Calendar.DATE, 29)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(alarmManager).set(AlarmManager.RTC_WAKEUP, nextBirthday.timeInMillis, pendingIntent)
    }

    @Test
    fun whenNoNotificationAndNextYearIsNotLeap() {
        calendarRepository.setDate(2000, Calendar.MARCH, 1)
        viewModel.switchBirthdayNotification(contact.copy(
            name = "Павел Павлович",
            dayOfBirthday = 29,
            monthOfBirthday = 2,
        ))
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2004)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DATE, 29)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(alarmManager).set(AlarmManager.RTC_WAKEUP, nextBirthday.timeInMillis, pendingIntent)
    }
}