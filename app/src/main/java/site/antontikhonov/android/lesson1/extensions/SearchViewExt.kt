package site.antontikhonov.android.lesson1.extensions

import androidx.appcompat.widget.SearchView
import io.reactivex.rxjava3.subjects.PublishSubject

fun SearchView.convertToObservable(): PublishSubject<String> {
    val subject: PublishSubject<String> = PublishSubject.create()
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }
        override fun onQueryTextChange(newText: String?): Boolean {
            if(newText != null) {
                subject.onNext(newText)
            }
            return false
        }
    })
    return subject
}