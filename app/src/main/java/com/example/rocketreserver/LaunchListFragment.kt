package com.example.rocketreserver

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.rx2.rxQuery
import com.example.rocketreserver.data.apolloClient
import com.example.rocketreserver.databinding.LaunchListFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LaunchListFragment : Fragment() {
    private lateinit var binding: LaunchListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LaunchListFragmentBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apolloClient.rxQuery(LaunchListQuery())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val launches = it?.data?.launches?.launches?.filterNotNull()

                if (launches != null && !it.hasErrors()) {
                    val adapter = LaunchListAdapter(launches)
                    binding.launches.layoutManager = LinearLayoutManager(requireContext())
                    binding.launches.adapter = adapter
                    adapter.onItemClicked = { launch ->
                        findNavController().navigate(
                                LaunchListFragmentDirections.openLaunchDetails(launchId = launch.id)
                        )
                    }
                }
            }

    }
}