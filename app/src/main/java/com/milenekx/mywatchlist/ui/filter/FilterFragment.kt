package com.milenekx.mywatchlist.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.Genre
import com.milenekx.mywatchlist.data.model.Country
import com.milenekx.mywatchlist.data.repository.Repository
import kotlinx.coroutines.launch
import androidx.navigation.fragment.navArgs // Import navArgs

class FilterFragment : Fragment() {

    private lateinit var chipGroupGenres: ChipGroup
    private lateinit var chipGroupCountries: ChipGroup
    private lateinit var etReleaseYear: EditText
    private lateinit var btnApplyFilters: Button
    private lateinit var btnResetFilters: Button
    private lateinit var progressBarGenres: ProgressBar
    private lateinit var progressBarCountries: ProgressBar
    private lateinit var tvFilterTitle: TextView

    private val repository = Repository()

    private var allGenres: List<Genre> = emptyList()
    private var allCountries: List<Country> = emptyList()


    private val args: FilterFragmentArgs by navArgs()
    private val filterType: String
        get() = args.filterType


    companion object {
        const val REQUEST_KEY_FILTERS = "request_key_filters"
        const val BUNDLE_KEY_GENRES = "bundle_key_genres"
        const val BUNDLE_KEY_YEAR = "bundle_key_year"
        const val BUNDLE_KEY_COUNTRIES = "bundle_key_countries"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize all UI elements
        chipGroupGenres = view.findViewById(R.id.chipGroupGenres)
        chipGroupCountries = view.findViewById(R.id.chipGroupCountries)
        etReleaseYear = view.findViewById(R.id.etReleaseYear)
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters)
        btnResetFilters = view.findViewById(R.id.btnResetFilters)
        progressBarGenres = view.findViewById(R.id.progressBarGenres)
        progressBarCountries = view.findViewById(R.id.progressBarCountries)
        tvFilterTitle = view.findViewById(R.id.tvFilterTitle)

        tvFilterTitle.text = if (filterType == "tv") "Filter TV Shows" else "Filter Movies"


        loadFilterOptions()
        setupListeners()
        prefillFilters()
    }

    private fun loadFilterOptions() {
        progressBarGenres.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = when (filterType) {
                    "tv" -> repository.getTvGenres()
                    else -> repository.getMovieGenres()
                }
                if (response.isSuccessful) {
                    allGenres = response.body()?.genres ?: emptyList()
                    populateGenreChips(allGenres)
                } else {
                    Toast.makeText(requireContext(), "Failed to load genres: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading genres: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBarGenres.visibility = View.GONE
            }
        }

        progressBarCountries.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = repository.getCountries()
                if (response.isSuccessful) {
                    allCountries = response.body() ?: emptyList()
                    populateCountryChips(allCountries)
                } else {
                    Toast.makeText(requireContext(), "Failed to load countries: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading countries: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBarCountries.visibility = View.GONE
            }
        }
    }


    private fun populateGenreChips(genres: List<Genre>) {
        chipGroupGenres.removeAllViews()
        genres.forEach { genre ->
            val chip = Chip(
                requireContext(),
                null,
                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter
            ).apply {
                text = genre.name
                tag = genre.id
                isCheckable = true
                isClickable = true
            }
            chipGroupGenres.addView(chip)
        }
        prefillSelectedChips()
    }

    private fun populateCountryChips(countries: List<Country>) {
        chipGroupCountries.removeAllViews()
        countries.sortedBy { it.englishName }.forEach { country ->
            val chip = Chip(
                requireContext(),
                null,
                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter
            ).apply {
                text = country.englishName
                tag = country.isoCode
                isCheckable = true
                isClickable = true
            }
            chipGroupCountries.addView(chip)
        }

        prefillSelectedChips()
    }


    private fun setupListeners() {
        btnApplyFilters.setOnClickListener {
            applyFilters()
        }

        btnResetFilters.setOnClickListener {
            resetFilters()
        }
    }

    private fun prefillFilters() {
        if (args.selectedYear != 0) {
            etReleaseYear.setText(args.selectedYear.toString())
        }
    }


    private fun prefillSelectedChips() {
        args.selectedGenreIds?.forEach { genreId: Int ->
            for (i in 0 until chipGroupGenres.childCount) {
                val chip = chipGroupGenres.getChildAt(i) as Chip
                if (chip.tag == genreId) {
                    chip.isChecked = true
                }
            }
        }

        args.selectedCountryCodes?.forEach { countryCode: String ->
            for (i in 0 until chipGroupCountries.childCount) {
                val chip = chipGroupCountries.getChildAt(i) as Chip
                if (chip.tag == countryCode) {
                    chip.isChecked = true

                }
            }
        }
    }


    private fun applyFilters() {
        val selectedGenreIds = chipGroupGenres.checkedChipIds.mapNotNull { id ->
            (chipGroupGenres.findViewById(id) as? Chip)?.tag as? Int
        }

        val releaseYear = etReleaseYear.text.toString().trim().toIntOrNull()

        val selectedCountryCodes = chipGroupCountries.checkedChipIds.mapNotNull { id ->
            (chipGroupCountries.findViewById(id) as? Chip)?.tag as? String
        }

        val resultBundle = bundleOf(
            BUNDLE_KEY_GENRES to ArrayList(selectedGenreIds),
            BUNDLE_KEY_YEAR to releaseYear,
            BUNDLE_KEY_COUNTRIES to ArrayList(selectedCountryCodes)

        )
        setFragmentResult(REQUEST_KEY_FILTERS, resultBundle)
        findNavController().popBackStack()
    }

    private fun resetFilters() {
        chipGroupGenres.clearCheck()
        chipGroupCountries.clearCheck()
        etReleaseYear.text.clear()

        val resultBundle = bundleOf(
            BUNDLE_KEY_GENRES to null,
            BUNDLE_KEY_YEAR to null,
            BUNDLE_KEY_COUNTRIES to null
        )
        setFragmentResult(REQUEST_KEY_FILTERS, resultBundle)
        findNavController().popBackStack()
    }
}