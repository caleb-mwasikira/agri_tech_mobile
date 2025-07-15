package com.example.agritech.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.agritech.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCurrentDateTime(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy MMM EEEE")
    return currentDateTime.format(formatter)
}

@Immutable
data class Note(
    val id: Int,
    val title: String,
    val description: String,
    @DrawableRes val imageUrl: Int,
)

val notes = mutableListOf(
    Note(
        id = 1,
        title = "From Seed to Sip. The Journey of a Tea Plant",
        description = "Explore the life cycle of a tea plant, from careful seed selection and nursery practices to the first flush of leaves ready for harvest.",
        imageUrl = R.drawable.tea
    ),
    Note(
        id = 2,
        title = "How Shade-Grown Coffee Enhances Flavor and Sustainability",
        description = "Discover why shade-grown coffee farms are becoming more popular and how they benefit both flavor quality and the environment",
        imageUrl = R.drawable.coffee_beans_24dp,
    ),
    Note(
        id = 3,
        title = "The Art of Hand-Harvesting Tea",
        description = "Go behind the scenes with tea pickers and learn how traditional hand-plucking ensures only the finest leaves make it to your cup",
        imageUrl = R.drawable.tea_harvesting,
    ),
    Note(
        id = 4,
        title = "Drying Beds and Sun-Kissed Beans",
        description = "Learn how traditional sun-drying methods on raised beds help preserve the rich flavor of freshly harvested coffee beans",
        imageUrl = R.drawable.coffee_beans_2,
    )
)