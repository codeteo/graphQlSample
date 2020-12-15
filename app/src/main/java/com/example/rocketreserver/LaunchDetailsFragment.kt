package com.example.rocketreserver

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.rxMutate
import com.apollographql.apollo.rx2.rxQuery
import com.example.rocketreserver.data.apolloClient
import com.example.rocketreserver.databinding.LaunchDetailsFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.function.BiConsumer

class LaunchDetailsFragment : Fragment() {

    private lateinit var binding: LaunchDetailsFragmentBinding
    val args: LaunchDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LaunchDetailsFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            binding.bookButton.visibility = View.GONE
            binding.bookProgressBar.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            binding.error.visibility = View.GONE

            apolloClient(requireContext()).rxQuery(LaunchDetailsQuery(id = args.launchId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                it.data?.launch?.let { launch ->
                                    binding.progressBar.visibility = View.GONE

                                    binding.missionPatch.load(launch.mission?.missionPatch) {
                                        placeholder(R.drawable.ic_placeholder)
                                    }
                                    binding.site.text = launch.site
                                    binding.missionName.text = launch.mission?.name
                                    val rocket = launch.rocket
                                    binding.rocketName.text = "🚀 ${rocket?.name} ${rocket?.type}"

                                    configureButton(launch.isBooked)
                                }

                            },
                            {
                                binding.progressBar.visibility = View.GONE
                                binding.error.text = it.message
                                binding.error.visibility = View.VISIBLE
                            }
                    )
        }
    }

    private fun configureButton(isBooked: Boolean) {
        binding.bookButton.visibility = View.VISIBLE
        binding.bookProgressBar.visibility = View.GONE

        binding.bookButton.text = if (isBooked) {
            getString(R.string.cancel)
        } else {
            getString(R.string.book_now)
        }

        binding.bookButton.setOnClickListener {
            val context = context
            if (context != null && User.getToken(context) == null) {
                findNavController().navigate(
                        R.id.open_login
                )
                return@setOnClickListener
            }

            val mutation = if (isBooked) {
                CancelTripMutation(id = args.launchId)
            } else {
                BookTripMutation(id = args.launchId)
            }

            apolloClient(requireContext()).rxMutate(mutation)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { res ->
                        run {
                            if (res.hasErrors()) {
                                configureButton(isBooked)
                            }
                            configureButton(!isBooked)
                        }
                    }

        }
    }
}