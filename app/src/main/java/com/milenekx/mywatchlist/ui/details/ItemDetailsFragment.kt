package com.milenekx.mywatchlist.ui.details

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.milenekx.mywatchlist.R
import com.milenekx.mywatchlist.data.model.ItemDetailsResponse
import com.milenekx.mywatchlist.viewmodel.ItemDetailsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ItemDetailsFragment : Fragment(R.layout.fragment_item_details) {

    private val viewModel: ItemDetailsViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }

    private val args: ItemDetailsFragmentArgs by navArgs()

    private lateinit var titleText: TextView
    private lateinit var taglineText: TextView
    private lateinit var overviewText: TextView
    private lateinit var releaseDateText: TextView
    private lateinit var runtimeText: TextView
    private lateinit var languageText: TextView
    private lateinit var genresText: TextView
    private lateinit var statusText: TextView
    private lateinit var homepageText: TextView
    private lateinit var votesText: TextView
    private lateinit var productionCompaniesText: TextView
    private lateinit var productionCountriesText: TextView
    private lateinit var posterImage: ImageView

    private var currentDetails: ItemDetailsResponse? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        titleText = view.findViewById(R.id.textTitle)
        taglineText = view.findViewById(R.id.textTagline)
        overviewText = view.findViewById(R.id.textOverview)
        releaseDateText = view.findViewById(R.id.textReleaseDate)
        runtimeText = view.findViewById(R.id.textRuntime)
        languageText = view.findViewById(R.id.textLanguage)
        genresText = view.findViewById(R.id.textGenres)
        statusText = view.findViewById(R.id.textStatus)
        homepageText = view.findViewById(R.id.textHomepage)
        votesText = view.findViewById(R.id.textVotes)
        productionCompaniesText = view.findViewById(R.id.textProductionCompanies)
        productionCountriesText = view.findViewById(R.id.textProductionCountries)
        posterImage = view.findViewById(R.id.imagePoster)

        val mediaId = args.mediaId
        val mediaType = args.mediaType

        lifecycleScope.launch {
            val itemDetails = when (mediaType.lowercase()) {
                "movie" -> viewModel.getMovieDetails(mediaId.toInt())
                "tv" -> viewModel.getTVDetails(mediaId.toInt())
                else -> null
            }

            itemDetails?.let { bindUI(it) } ?: Log.e("ItemDetailsFragment", "No details found")
        }
    }


    private fun bindUI(details: ItemDetailsResponse) {
        currentDetails = details

        titleText.text = details.title ?: details.name ?: "Untitled"
        overviewText.text = details.overview
        taglineText.text = details.tagline ?: ""

        val rawDate = details.releaseDate ?: details.firstAirDate
        val formattedDate = rawDate?.let { formatDateToReadable(it) } ?: "N/A"
        releaseDateText.text = "Release Date: $formattedDate"

        val runtimeMinutes = details.runtime ?: details.episodeRunTime?.firstOrNull()
        val formattedRuntime = runtimeMinutes?.let { formatRuntime(it) } ?: "N/A"
        runtimeText.text = "Runtime: $formattedRuntime"

        val formattedLanguage = Locale(details.originalLanguage).getDisplayLanguage(Locale.ENGLISH)
        languageText.text = "Language: $formattedLanguage"

        genresText.text = "Genres: ${details.genres?.joinToString(", ") { it.name } ?: "N/A"}"
        statusText.text = "Status: ${details.status ?: "N/A"}"
        homepageText.text = details.homepage ?: "No homepage"
        votesText.text = "Rating: ${details.voteAverage} (${details.voteCount} votes)"

        productionCompaniesText.apply {
            val value = details.productionCompanies?.joinToString(", ") { it.name }
            text = "Production Companies: $value"
            visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        productionCountriesText.apply {
            val value = details.productionCountries?.joinToString(", ") { it.name }
            text = "Countries: $value"
            visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
        }


        val fullPosterUrl = "https://image.tmdb.org/t/p/w500${details.posterPath}"
        Glide.with(requireContext())
            .load(fullPosterUrl)
            .placeholder(R.drawable.rounded_corner)
            .into(posterImage)
    }


    private fun formatDateToReadable(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = parser.parse(dateString)
            val day = date?.let { SimpleDateFormat("d", Locale.ENGLISH).format(it).toInt() }
            val suffix = day?.let { getDaySuffix(it) }
            val formatted = date?.let { SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH).format(it) }
            "$day$suffix $formatted"
        } catch (e: Exception) {
            "N/A"
        }
    }

    private fun getDaySuffix(day: Int): String {
        return if (day in 11..13) "th" else when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    private fun formatRuntime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return "${if (hours > 0) "${hours}h " else ""}${mins}min"
    }

}
