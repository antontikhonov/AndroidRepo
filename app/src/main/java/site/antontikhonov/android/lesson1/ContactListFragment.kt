package site.antontikhonov.android.lesson1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {

    private var recyclerView: RecyclerView? = null
    private var adapter: ContactListAdapter? = null
    private var contactDecorator: ContactDecorator? = null
    private var displayer: AlertDialogFragment.AlertDialogDisplayer? = null
    private var onItemClickListener: ContactListAdapter.OnItemClickListener? = null
    private var viewModel: ContactListViewModel? = null
    private var progressBar: ProgressBar? = null

    val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    loadContacts()
                } else {
                    when {
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                            displayer?.displayAlertDialog(R.string.no_permissions_dialog_list)
                        }
                        else -> {
                            showNoContactPermissionSnackbar()
                        }
                    }
                }
            }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AlertDialogFragment.AlertDialogDisplayer) {
            displayer = context
        }
        if(context is ContactListAdapter.OnItemClickListener) {
            onItemClickListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactListViewModel::class.java)
        setHasOptionsMenu(true)
        val drawableDivider = ContextCompat.getDrawable(requireContext().applicationContext, R.drawable.divider)
        if(drawableDivider != null) {
            val offsetPx = resources.getDimensionPixelSize(R.dimen.main_padding)
            contactDecorator = ContactDecorator(drawableDivider, offsetPx)
        }
        adapter = ContactListAdapter { id -> onItemClickListener?.clickItem(id) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_list_title)
        progressBar = view.findViewById(R.id.progress_bar_contact_list)
        initializeRecyclerView(view)
        viewModel?.contacts?.observe(viewLifecycleOwner, Observer { adapter?.submitList(it) })
        viewModel?.isLoading?.observe(viewLifecycleOwner, Observer { isLoading ->
            when(isLoading) {
                true -> progressBar?.visibility = View.VISIBLE
                false -> progressBar?.visibility = View.GONE
            }
        })
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onDestroyView() {
        recyclerView?.adapter = null
        recyclerView = null
        progressBar = null
        super.onDestroyView()
    }

    override fun onDetach() {
        displayer = null
        requestPermissionLauncher.unregister()
        super.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.appSearchBar)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        viewModel?.searchContact(requireContext(), RxSearchObservable.fromView(searchView))
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun checkPermission() {
        val isStartCheckPermission = arguments?.getBoolean(EXTRA_START_CHECK_PERMISSION) ?: true
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED -> {
                loadContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                displayer?.displayAlertDialog(R.string.no_permissions_dialog_list)
            }
            else -> {
                if (isStartCheckPermission) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        }
    }

    private fun initializeRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        with(recyclerView) {
            this?.setHasFixedSize(true)
            this?.layoutManager = LinearLayoutManager(context)
            this?.adapter = adapter
            contactDecorator?.let { this?.addItemDecoration(it) }
        }
    }

    private fun loadContacts() = viewModel?.loadContactList(requireContext())

    private fun showNoContactPermissionSnackbar() {
        Snackbar.make(requireView(), R.string.snackbar_title_list, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.snackbar_button) {
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse(URI_PACKAGE_SCHEME + requireActivity().packageName)
                )
                startActivity(appSettingsIntent)
            }
            .show()
    }

    companion object {
        fun newInstance(isStartCheckPermission: Boolean): ContactListFragment {
            val args = Bundle()
            args.putBoolean(EXTRA_START_CHECK_PERMISSION, isStartCheckPermission)
            val fragment = ContactListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}