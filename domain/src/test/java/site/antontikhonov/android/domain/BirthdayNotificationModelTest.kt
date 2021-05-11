package site.antontikhonov.android.domain

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import site.antontikhonov.android.domain.contactdetails.ContactDetailsEntity
import site.antontikhonov.android.domain.notification.*
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class BirthdayNotificationModelTest {

    @Mock
    private lateinit var interactor: BirthdayNotificationInteractor
    private lateinit var calendarRepository: CalendarRepository
    private lateinit var mockedBirthdayNotificationRepository: BirthdayNotificationRepository
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
        mockedBirthdayNotificationRepository = mock(BirthdayNotificationRepository::class.java)
        calendarRepository = CalendarRepositoryImpl()
        interactor = BirthdayNotificationModel(mockedBirthdayNotificationRepository, calendarRepository)
    }

    @Test
    fun whenNoNotificationAndSetOnNextYear() {
        `when`(mockedBirthdayNotificationRepository.checkNotification(contact.id)).thenReturn(true)
        calendarRepository.setDate(1999, Calendar.SEPTEMBER, 9)
        interactor.switchBirthdayNotification(
            contact.id,
            contact.name,
            contact.dayOfBirthday,
            contact.monthOfBirthday - 1
        )
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2000)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DATE, 8)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(mockedBirthdayNotificationRepository).makeNotification(contact.id, contact.name, nextBirthday)
    }

    @Test
    fun whenNoNotificationAndSetOnCurrentYear() {
        `when`(mockedBirthdayNotificationRepository.checkNotification(contact.id)).thenReturn(true)
        calendarRepository.setDate(1999, Calendar.SEPTEMBER, 7)
        interactor.switchBirthdayNotification(
            contact.id,
            contact.name,
            contact.dayOfBirthday,
            contact.monthOfBirthday - 1
        )
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 1999)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DATE, 8)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(mockedBirthdayNotificationRepository).makeNotification(contact.id, contact.name, nextBirthday)
    }

    @Test
    fun whenHaveNotificationAndRemoveIt() {
        `when`(mockedBirthdayNotificationRepository.checkNotification(contact.id)).thenReturn(false)
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
        mockedBirthdayNotificationRepository.makeNotification(contact.id, contact.name, nextBirthday)
        interactor.switchBirthdayNotification(
            contact.id,
            contact.name,
            contact.dayOfBirthday,
            contact.monthOfBirthday - 1
        )
        verify(mockedBirthdayNotificationRepository).removeNotification(contact.id)
    }

    @Test
    fun whenNoNotificationAndNextYearIsLeap() {
        val contact = contact.copy(
            name = "Павел Павлович",
            dayOfBirthday = 29,
            monthOfBirthday = 2,
        )
        `when`(mockedBirthdayNotificationRepository.checkNotification(contact.id)).thenReturn(true)
        calendarRepository.setDate(1999, Calendar.MARCH, 2)
        interactor.switchBirthdayNotification(
            contact.id,
            contact.name,
            contact.dayOfBirthday,
            contact.monthOfBirthday - 1
        )
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2000)
            set(Calendar.MONTH,  Calendar.FEBRUARY)
            set(Calendar.DATE, 29)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(mockedBirthdayNotificationRepository).makeNotification(contact.id, contact.name, nextBirthday)
    }

    @Test
    fun whenNoNotificationAndNextYearIsNotLeap() {
        val contact = contact.copy(
            name = "Павел Павлович",
            dayOfBirthday = 29,
            monthOfBirthday = 2,
        )
        `when`(mockedBirthdayNotificationRepository.checkNotification(contact.id)).thenReturn(true)
        calendarRepository.setDate(2000, Calendar.MARCH, 1)
        interactor.switchBirthdayNotification(
            contact.id,
            contact.name,
            contact.dayOfBirthday,
            contact.monthOfBirthday - 1
        )
        val nextBirthday = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2004)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DATE, 29)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        verify(mockedBirthdayNotificationRepository).makeNotification(contact.id, contact.name, nextBirthday)
    }
}