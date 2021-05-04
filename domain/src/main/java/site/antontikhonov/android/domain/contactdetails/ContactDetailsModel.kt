package site.antontikhonov.android.domain.contactdetails

class ContactDetailsModel(private val repository: ContactDetailsRepository)
    : ContactDetailsInteractor {
    override fun loadContactById(id: String) = repository.readContactById(id)
}