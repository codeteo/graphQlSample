package com.example.rocketreserver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apollographql.apollo.rx2.rxQuery
import com.example.rocketreserver.data.ApiService
import com.example.rocketreserver.data.apolloClient
import com.example.rocketreserver.data.retrofitClient
import com.example.rocketreserver.databinding.LaunchListFragmentBinding
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class LaunchListFragment : Fragment() {
    private lateinit var binding: LaunchListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LaunchListFragmentBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApollo.setOnClickListener {
            apolloClient(requireContext())
                    .rxQuery(LaunchListQuery())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val launches = it?.data?.launches?.launches?.filterNotNull()
                        if (launches != null && !it.hasErrors()) {
                            Toast.makeText(requireContext(), "Apollo Response", Toast.LENGTH_SHORT).show()
                        }
                    }
        }

        binding.btnRetrofit.setOnClickListener {
            retrofitClient(requireContext())
                    .create(ApiService::class.java)
                    .getLaunches(createRequestBody())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val launches = it?.body()?.launches?.launches?.filterNotNull()
                        if (launches != null && it.isSuccessful) {
                            Toast.makeText(requireContext(), "Retrofit Response", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun createRequestBody(): RequestBody {
        val json = JSONObject()
        json.put("operationName", "LaunchList")
        json.put("variables", "{}")
        json.put("query", "\"query LaunchList { launches { __typename hasMore cursor launches { __typename id site mission { __typename name missionPatch(size: SMALL) } } } }\"")

        return RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
        )
    }
}