package com.milenekx.mywatchlist.ui.tv

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.repository.Repository
import com.milenekx.mywatchlist.ui.adapter.MediaGridAdapter
import com.milenekx.mywatchlist.viewmodel.GridViewModel
import com.milenekx.mywatchlist.viewmodel.GridViewModelFactory
import com.milenekx.mywatchlist.ui.filter.FilterFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.milenekx.mywatchlist.util.navigateToItemDetails

@Suppress("DEPRECATION")
class TVFragment : Fragment(R.layout.fragment_tv) {

    private lateinit var viewModel: GridViewModel
    private lateinit var adapter: MediaGridAdapter

    private lateinit var chipLatest: Chip
    private lateinit var chipTrending: Chip
    private lateinit var chipPopular: Chip
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var loadingOverlay: FrameLayout
    private lateinit var btnFilter: ImageButton
    private lateinit var btnSearch: ImageButton

    private var isProgrammaticChange = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTvGrid)
        chipLatest = view.findViewById(R.id.chipLatest)
        chipTrending = view.findViewById(R.id.chipTrending)
        chipPopular = view.findViewById(R.id.chipPopular)
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories)
        loadingOverlay = view.findViewById(R.id.loadingOverlay)
        btnFilter = view.findViewById(R.id.btnFilter)
        btnSearch = view.findViewById(R.id.btnSearch)

        adapter = MediaGridAdapter(emptyList()) { item ->
            navigateToItemDetails(item, findNavController())
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = adapter

        val factory = GridViewModelFactory(Repository())
        viewModel = ViewModelProvider(this, factory)[GridViewModel::class.java]

        viewModel.gridItems.observe(viewLifecycleOwner) { items ->
            adapter.updateData(items)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        setupChipListeners()
        setupFilterButtonListener()
        setupFilterResultListener()
        setupSearchButtonListener()

        if (savedInstanceState == null) {
            val hasActiveFilters = viewModel.currentFilterGenres != null ||
                    viewModel.currentFilterYear != null ||
                    viewModel.currentFilterCountries != null

            if (hasActiveFilters && viewModel.currentCategoryType == "filtered") {
                viewModel.fetchFilteredTvShows(
                    genres = viewModel.currentFilterGenres,
                    year = viewModel.currentFilterYear,
                    countries = viewModel.currentFilterCountries
                )
            } else if (viewModel.gridItems.value.isNullOrEmpty()) {
                viewModel.fetchGridItems(type = "latest", mediaType = "tv")
                chipLatest.isChecked = true
            }
        } else {
            isProgrammaticChange = true
            when (viewModel.currentCategoryType) {
                "latest" -> chipLatest.isChecked = true
                "trending" -> chipTrending.isChecked = true
                "popular" -> chipPopular.isChecked = true
                "filtered" -> {
                    chipGroupCategories.clearCheck()
                    chipLatest.isChecked = false
                    chipTrending.isChecked = false
                    chipPopular.isChecked = false

                    viewModel.fetchFilteredTvShows(
                        genres = viewModel.currentFilterGenres,
                        year = viewModel.currentFilterYear,
                        countries = viewModel.currentFilterCountries
                    )
                }
            }
            isProgrammaticChange = false
        }
    }

    private fun setupChipListeners() {
        chipLatest.setOnCheckedChangeListener { _, isChecked ->
            if (!isProgrammaticChange && isChecked && viewModel.currentCategoryType != "latest") {
                viewModel.fetchGridItems("latest", "tv")
            }
        }

        chipTrending.setOnCheckedChangeListener { _, isChecked ->
            if (!isProgrammaticChange && isChecked && viewModel.currentCategoryType != "trending") {
                viewModel.fetchGridItems("trending", "tv")
            }
        }

        chipPopular.setOnCheckedChangeListener { _, isChecked ->
            if (!isProgrammaticChange && isChecked && viewModel.currentCategoryType != "popular") {
                viewModel.fetchGridItems("popular", "tv")
            }
        }
    }

    private fun setupFilterButtonListener() {
        btnFilter.setOnClickListener {
            val action = TVFragmentDirections.actionTvFragmentToFilterFragment(
                selectedGenreIds = viewModel.currentFilterGenres?.toIntArray(),
                selectedYear = viewModel.currentFilterYear ?: 0,
                selectedCountryCodes = viewModel.currentFilterCountries?.toTypedArray(),
                filterType = "tv"
            )
            findNavController().navigate(action)
        }
    }

    private fun setupSearchButtonListener() {
        btnSearch.setOnClickListener {
            val action = TVFragmentDirections.actionTvFragmentToSearchFragment("tv")
            findNavController().navigate(action)
        }
    }

    private fun setupFilterResultListener() {
        setFragmentResultListener(FilterFragment.REQUEST_KEY_FILTERS) { requestKey, bundle ->
            if (requestKey == FilterFragment.REQUEST_KEY_FILTERS) {
                val selectedGenreIds = bundle.getIntegerArrayList(FilterFragment.BUNDLE_KEY_GENRES)
                val selectedYear = bundle.get(FilterFragment.BUNDLE_KEY_YEAR) as? Int
                val selectedCountryCodes = bundle.getStringArrayList(FilterFragment.BUNDLE_KEY_COUNTRIES)

                val filtersAreCleared = selectedGenreIds.isNullOrEmpty()
                        && selectedYear == null
                        && selectedCountryCodes.isNullOrEmpty()

                isProgrammaticChange = true
                chipGroupCategories.clearCheck()
                chipLatest.isChecked = false
                chipTrending.isChecked = false
                chipPopular.isChecked = false
                isProgrammaticChange = false

                adapter.updateData(emptyList())
                loadingOverlay.visibility = View.VISIBLE

                if (filtersAreCleared) {
                    chipLatest.isChecked = true
                    viewModel.fetchGridItems("latest", "tv")
                } else {
                    viewModel.fetchFilteredTvShows(
                        genres = selectedGenreIds,
                        year = selectedYear,
                        countries = selectedCountryCodes
                    )
                }
            }
        }
    }
}
