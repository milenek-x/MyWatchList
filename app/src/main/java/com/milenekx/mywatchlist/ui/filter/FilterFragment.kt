// com.milenekx.mywatchlist.ui.filter.FilterFragment.kt

package com.milenekx.mywatchlist.ui.filter

import android.os.Bundle
import android.util.Log
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
import com.milenekx.mywatchlist.data.repository.MovieRepository
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

    // You might consider a ViewModel for FilterFragment if it needs more complex state management
    // For now, direct repository access is acceptable for fetching static lists.
    private val repository = MovieRepository()

    private var allGenres: List<Genre> = emptyList()
    private var allCountries: List<Country> = emptyList()


    // Use navArgs to retrieve arguments passed from MoviesFragment
    private val args: FilterFragmentArgs by navArgs()
    private val filterType: String
        get() = args.filterType


    companion object {
        const val REQUEST_KEY_FILTERS = "request_key_filters"
        const val BUNDLE_KEY_GENRES = "bundle_key_genres" // List<Int> genre IDs
        const val BUNDLE_KEY_YEAR = "bundle_key_year"     // Int year
        const val BUNDLE_KEY_COUNTRIES = "bundle_key_countries" // List<String> country codes (ISO)
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


        loadFilterOptions() // Fetches genres and countries, then populates chips and pre-fills
        setupListeners()
        prefillFilters() // Prefills the year EditText
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

        // Countries likely the same, no change needed
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
        chipGroupGenres.removeAllViews() // Clear existing chips before adding new ones
        genres.forEach { genre ->
            val chip = Chip(
                requireContext(),
                null, // AttributeSet, can be null
                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter // Correct way to apply style
            ).apply {
                text = genre.name
                tag = genre.id // Store genre ID in tag for easy retrieval later
                isCheckable = true
                isClickable = true
            }
            chipGroupGenres.addView(chip)
        }
        // After populating, prefill the selected ones from arguments
        prefillSelectedChips()
    }

    private fun populateCountryChips(countries: List<Country>) {
        chipGroupCountries.removeAllViews() // Clear existing chips before adding new ones
        // Sort countries alphabetically for better UX
        countries.sortedBy { it.english_name }.forEach { country ->
            val chip = Chip(
                requireContext(),
                null, // AttributeSet, can be null
                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter // Correct way to apply style
            ).apply {
                text = country.english_name
                tag = country.iso_3166_1 // Store ISO code in tag
                isCheckable = true
                isClickable = true
            }
            chipGroupCountries.addView(chip)
        }
        // After populating, prefill the selected ones from arguments
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

    // Prefills the year EditText based on arguments
    private fun prefillFilters() {
        if (args.selectedYear != 0) { // Default value for int argument is 0
            etReleaseYear.setText(args.selectedYear.toString())
        }
    }

    // Prefills the genre and country chips after they have been populated dynamically
    private fun prefillSelectedChips() {
        // Prefill genres
        args.selectedGenreIds?.forEach { genreId: Int -> // Explicitly type lambda parameter for clarity
            for (i in 0 until chipGroupGenres.childCount) {
                val chip = chipGroupGenres.getChildAt(i) as Chip
                if (chip.tag == genreId) {
                    chip.isChecked = true
                    // No break here, as multiple genres can be selected
                }
            }
        }

        // Prefill countries
        args.selectedCountryCodes?.forEach { countryCode: String -> // Explicitly type lambda parameter for clarity
            for (i in 0 until chipGroupCountries.childCount) {
                val chip = chipGroupCountries.getChildAt(i) as Chip
                if (chip.tag == countryCode) {
                    chip.isChecked = true
                    // No break here, as multiple countries can be selected
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

        // Pass results back using Fragment Result API
        val resultBundle = bundleOf(
            BUNDLE_KEY_GENRES to ArrayList(selectedGenreIds), // Must be ArrayList for bundle
            BUNDLE_KEY_YEAR to releaseYear,
            BUNDLE_KEY_COUNTRIES to ArrayList(selectedCountryCodes) // Must be ArrayList for bundle

        )
        setFragmentResult(REQUEST_KEY_FILTERS, resultBundle)
        findNavController().popBackStack() // Go back to the previous fragment
    }

    private fun resetFilters() {
        chipGroupGenres.clearCheck()
        chipGroupCountries.clearCheck()
        etReleaseYear.text.clear()

        // Send back nulls to indicate no filters
        val resultBundle = bundleOf(
            BUNDLE_KEY_GENRES to null,
            BUNDLE_KEY_YEAR to null,
            BUNDLE_KEY_COUNTRIES to null
        )
        setFragmentResult(REQUEST_KEY_FILTERS, resultBundle)
        findNavController().popBackStack()
    }
}