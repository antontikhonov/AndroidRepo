package site.antontikhonov.android.lesson1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

private const val CONTACT_ID : Int = 7

class MainActivity : AppCompatActivity(), ContactListFragment.FragmentClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null) {
            addContactListFragment()
        }
    }

    private fun addContactListFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragments_container, ContactListFragment())
            .commit()
    }

    private fun addContactDetailsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_container, ContactDetailsFragment.newInstance(CONTACT_ID))
            .addToBackStack(null)
            .commit()
    }

    override fun onClickFragment() {
        addContactDetailsFragment()
    }
}