package com.example.aviatickets.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aviatickets.R
import com.example.aviatickets.adapter.OfferListAdapter
import com.example.aviatickets.databinding.FragmentOfferListBinding
import com.example.aviatickets.model.entity.Offer
import com.example.aviatickets.model.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OfferListFragment : Fragment() {

    private var _binding: FragmentOfferListBinding? = null
    private val binding get() = _binding!!

    private val adapter: OfferListAdapter by lazy {
        OfferListAdapter()
    }
    private var currentSortOption: Int = -1 // Initialize with an invalid value

    companion object {
        fun newInstance() = OfferListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfferListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        fetchOfferList()
    }

    private fun fetchOfferList() {
        ApiClient.apiService.fetchOfferList().enqueue(object : Callback<List<Offer>> {
            override fun onResponse(call: Call<List<Offer>>, response: Response<List<Offer>>) {
                if (response.isSuccessful) {
                    val offerList = response.body()
                    offerList?.let {
                        // Apply the initial sorting based on the default option
                        if (currentSortOption != -1) {
                            sortOfferList(currentSortOption)
                        } else {
                            adapter.submitList(it)
                        }
                    }
                } else {
                    // Handle error
                    Log.e("OfferListFragment", "Failed to fetch offer list")
                }
            }

            override fun onFailure(call: Call<List<Offer>>, t: Throwable) {
                // Handle failure
                Log.e("OfferListFragment", "Error fetching offer list", t)
            }
        })
    }

    private fun setupUI() {
        with(binding) {
            offerList.adapter = adapter

            sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                // Update the sort option and apply sorting
                currentSortOption = checkedId
                sortOfferList(checkedId)
            }
        }
    }

    private fun sortOfferList(checkedId: Int) {
        when (checkedId) {
            R.id.sort_by_price -> {
                adapter.submitList(adapter.currentList.sortedBy { it.price })
            }
            R.id.sort_by_duration -> {
                adapter.submitList(adapter.currentList.sortedBy { it.flight.duration })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentOfferListBinding.inflate(layoutInflater, container, false)
//        return _binding?.root
//    }
//
//    private fun setupUI() {
//        with(binding) {
//            offerList.adapter = adapter
//
//            sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//                when (checkedId) {
//                    R.id.sort_by_price -> {
//                        /**
//                         * implement sorting by price
//                         */
//                    }
//
//                    R.id.sort_by_duration -> {
//                        /**
//                         * implement sorting by duration
//                         */
//                    }
//                }
//            }
//        }
//    }
//}