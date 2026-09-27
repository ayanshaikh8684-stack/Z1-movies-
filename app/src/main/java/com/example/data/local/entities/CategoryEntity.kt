package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MovieCategory

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val movieCount: Int,
    val gradientStart: Long,
    val gradientEnd: Long,
    val icon: String,
    val description: String = ""
) {
    fun toMovieCategory(): MovieCategory = MovieCategory(
        id = id,
        name = name,
        icon = icon,
        movieCount = movieCount,
        gradientStart = gradientStart,
        gradientEnd = gradientEnd,
        description = description
    )

    companion object {
        fun fromCategory(category: MovieCategory): CategoryEntity = CategoryEntity(
            id = category.id,
            name = category.name,
            movieCount = category.movieCount,
            gradientStart = category.gradientStart,
            gradientEnd = category.gradientEnd,
            icon = category.icon,
            description = category.description
        )
    }
}
