package com.example.demomvc.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.demomvc.R
import com.example.demomvc.database.UserDatabase
import com.example.demomvc.databinding.FragmentMainBinding
import com.example.demomvc.viewmodel.UsersViewModel
import com.example.demomvc.viewmodel.UsersViewModelFactory
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_main, container, false)

        val binding: FragmentMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = UserDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = UsersViewModelFactory(dataSource, application)
        val usersViewModel = ViewModelProvider(this, viewModelFactory).get(UsersViewModel::class.java)

        val adapter = UsersAdapter()
        binding.usersList.adapter = adapter

        usersViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                usersViewModel.doneShowingSnackbar()
            }
        })

        usersViewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.lifecycleOwner = this
        binding.viewModel = usersViewModel
        return binding.root
    }

}
