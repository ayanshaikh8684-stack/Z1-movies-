package com.example.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.Movie
import com.example.data.model.MovieCategory
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.GridMovieCard
import com.example.ui.components.HeroBanner
import com.example.ui.components.HorizontalMovieCard
import com.example.ui.components.SectionHeader
import com.example.ui.screens.categories.CategoryCard

@Composable
fun HomeScreen(
    movies: List<Movie>,
    continueWatching: List<ContinueWatchingItem>,
    watchlistMovieIds: Set<String>,
    banners: List<com.example.data.model.BannerItem> = emptyList(),
    categories: List<MovieCategory> = emptyList(),
    isPremiumUser: Boolean = false,
    onUpgradeClick: () -> Unit = {},
    onCategoryClick: (MovieCategory) -> Unit = {},
    onMovieClick: (Movie) -> Unit,
    onWatchNowClick: (Movie) -> Unit,
    onAddToListClick: (Movie) -> Unit,
    onResumeWatchingClick: (ContinueWatchingItem) -> Unit,
    onSeeAllSection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredMovies = movies.filter { it.isFeatured }
    val trendingMovies = movies.filter { it.isTrending }.sortedBy { it.trendingRank ?: 99 }
    val newReleases = movies.filter { it.isNewRelease }
    val popularMovies = movies.sortedByDescending { it.rating }
    val recommendedMovies = movies.shuffled().take(6)
    val recentlyAdded = movies.filter { it.isRecentlyAdded || it.isNewRelease }.take(6)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Large Cinematic Hero Banner (dynamic from Firestore banners / featured movies)
        item {
            HeroBanner(
                featuredMovies = if (featuredMovies.isNotEmpty()) featuredMovies else movies.take(3),
                watchlistMovieIds = watchlistMovieIds,
                banners = banners,
                onWatchNowClick = onWatchNowClick,
                onAddToListClick = onAddToListClick,
                onMovieClick = onMovieClick
            )
        }

        // Categories Carousel
        if (categories.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Explore Categories",
                    subtitle = "Browse by cinematic theme & genre",
                    onSeeAllClick = { onSeeAllSection("Categories") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories, key = { it.id }) { cat ->
                        Box(modifier = Modifier.width(150.dp)) {
                            CategoryCard(
                                category = cat,
                                onClick = { onCategoryClick(cat) }
                            )
                        }
                    }
                }
            }
        }

        // 2. Continue Watching Section
        if (continueWatching.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Continue Watching",
                    subtitle = "Pick up right where you left off"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(continueWatching, key = { it.movieId }) { item ->
                        ContinueWatchingCard(
                            item = item,
                            onResumeClick = { onResumeWatchingClick(item) }
                        )
                    }
                }
            }
        }

        // 3. Trending Now Section (with subtle ranking number)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Trending Now",
                subtitle = "Top 10 most watched this week",
                onSeeAllClick = { onSeeAllSection("Trending") }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(trendingMovies, key = { _, m -> m.id }) { index, movie ->
                    HorizontalMovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        rankingNumber = index + 1,
                        isBookmarked = watchlistMovieIds.contains(movie.id),
                        onBookmarkToggle = { onAddToListClick(movie) }
                    )
                }
            }
        }

        // 4. New Releases Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = "New Releases",
                subtitle = "Fresh licensed cinema",
                onSeeAllClick = { onSeeAllSection("New Releases") }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(newReleases, key = { it.id }) { movie ->
                    HorizontalMovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        isBookmarked = watchlistMovieIds.contains(movie.id),
                        onBookmarkToggle = { onAddToListClick(movie) }
                    )
                }
            }
        }

        // AdMob-Ready Native Banner placement (hidden for VIP subscribers)
        item {
            com.example.ui.components.AdMobBannerView(
                isPremiumUser = isPremiumUser,
                onUpgradeClick = onUpgradeClick
            )
        }

        // 5. Popular Movies (2-Column Grid on standard phones)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = "Popular Movies",
                subtitle = "Critically acclaimed & high rated",
                onSeeAllClick = { onSeeAllSection("Popular") }
            )
        }

        val gridPairs = popularMovies.take(6).chunked(2)
        items(gridPairs) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    GridMovieCard(
                        movie = pair[0],
                        onClick = { onMovieClick(pair[0]) },
                        isBookmarked = watchlistMovieIds.contains(pair[0].id),
                        onBookmarkToggle = { onAddToListClick(pair[0]) }
                    )
                }
                if (pair.size > 1) {
                    Column(modifier = Modifier.weight(1f)) {
                        GridMovieCard(
                            movie = pair[1],
                            onClick = { onMovieClick(pair[1]) },
                            isBookmarked = watchlistMovieIds.contains(pair[1].id),
                            onBookmarkToggle = { onAddToListClick(pair[1]) }
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 6. Recommended For You
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Recommended For You",
                subtitle = "Curated based on your interests",
                onSeeAllClick = { onSeeAllSection("Recommended") }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendedMovies, key = { it.id }) { movie ->
                    HorizontalMovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        isBookmarked = watchlistMovieIds.contains(movie.id),
                        onBookmarkToggle = { onAddToListClick(movie) }
                    )
                }
            }
        }

        // 7. Recently Added
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Recently Added",
                subtitle = "Latest arrivals to Z1 catalog",
                onSeeAllClick = { onSeeAllSection("Recently Added") }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentlyAdded, key = { it.id }) { movie ->
                    HorizontalMovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        isBookmarked = watchlistMovieIds.contains(movie.id),
                        onBookmarkToggle = { onAddToListClick(movie) }
                    )
                }
            }
        }

        // 8. Coming Soon / Upcoming Releases
        val comingSoonMovies = movies.filter { it.isScheduledRelease || it.publishAt != null || it.digitalReleaseDate.isNotBlank() }
        if (comingSoonMovies.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(
                    title = "Coming Soon",
                    subtitle = "Upcoming premieres & scheduled releases",
                    onSeeAllClick = { onSeeAllSection("Coming Soon") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comingSoonMovies, key = { it.id }) { movie ->
                        HorizontalMovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) },
                            isBookmarked = watchlistMovieIds.contains(movie.id),
                            onBookmarkToggle = { onAddToListClick(movie) }
                        )
                    }
                }
            }
        }
    }
}
