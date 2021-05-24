package site.antontikhonov.android.presentation.schedulers

import io.reactivex.rxjava3.core.Scheduler

interface BaseSchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
}