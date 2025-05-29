package com.milenekx.mywatchlist.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun openWebPage(context: Context, url: String?) {
    if (url.isNullOrBlank()) {
        Toast.makeText(context, "No homepage available", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open link: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}