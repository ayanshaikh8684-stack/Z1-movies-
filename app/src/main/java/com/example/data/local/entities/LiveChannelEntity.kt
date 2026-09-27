package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EpgProgram
import com.example.data.model.LiveChannel
import com.example.data.model.LiveChannelReport

@Entity(tableName = "live_channels")
data class LiveChannelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val logoUrl: String,
    val description: String = "",
    val category: String,
    val language: String = "English",
    val country: String = "Global",
    val streamUrl: String,
    val epgSourceUrl: String = "",
    val currentProgram: String = "Live Broadcast",
    val nextProgram: String = "Upcoming Show",
    val currentProgramTime: String = "Live Now",
    val isFeatured: Boolean = false,
    val isPublished: Boolean = true,
    val isEnabled: Boolean = true,
    val isPremiumOnly: Boolean = false,
    val displayOrder: Int = 0,
    val rightsProvider: String = "Authorized Broadcaster",
    val territory: String = "Worldwide / Open Broadcast",
    val rightsStartDate: Long? = null,
    val rightsExpiryDate: Long? = null,
    val isStreamingPermitted: Boolean = true,
    val isAdSupportedPermitted: Boolean = true,
    val isPremiumAccessPermitted: Boolean = true,
    val schedule: List<EpgProgram> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): LiveChannel = LiveChannel(
        id = id,
        name = name,
        logoUrl = logoUrl,
        description = description,
        category = category,
        language = language,
        country = country,
        streamUrl = streamUrl,
        epgSourceUrl = epgSourceUrl,
        currentProgram = currentProgram,
        nextProgram = nextProgram,
        currentProgramTime = currentProgramTime,
        isFeatured = isFeatured,
        isPublished = isPublished,
        isEnabled = isEnabled,
        isPremiumOnly = isPremiumOnly,
        displayOrder = displayOrder,
        rightsProvider = rightsProvider,
        territory = territory,
        rightsStartDate = rightsStartDate,
        rightsExpiryDate = rightsExpiryDate,
        isStreamingPermitted = isStreamingPermitted,
        isAdSupportedPermitted = isAdSupportedPermitted,
        isPremiumAccessPermitted = isPremiumAccessPermitted,
        schedule = schedule,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(channel: LiveChannel): LiveChannelEntity = LiveChannelEntity(
            id = channel.id,
            name = channel.name,
            logoUrl = channel.logoUrl,
            description = channel.description,
            category = channel.category,
            language = channel.language,
            country = channel.country,
            streamUrl = channel.streamUrl,
            epgSourceUrl = channel.epgSourceUrl,
            currentProgram = channel.currentProgram,
            nextProgram = channel.nextProgram,
            currentProgramTime = channel.currentProgramTime,
            isFeatured = channel.isFeatured,
            isPublished = channel.isPublished,
            isEnabled = channel.isEnabled,
            isPremiumOnly = channel.isPremiumOnly,
            displayOrder = channel.displayOrder,
            rightsProvider = channel.rightsProvider,
            territory = channel.territory,
            rightsStartDate = channel.rightsStartDate,
            rightsExpiryDate = channel.rightsExpiryDate,
            isStreamingPermitted = channel.isStreamingPermitted,
            isAdSupportedPermitted = channel.isAdSupportedPermitted,
            isPremiumAccessPermitted = channel.isPremiumAccessPermitted,
            schedule = channel.schedule,
            createdAt = channel.createdAt
        )
    }
}

@Entity(tableName = "live_channel_reports")
data class LiveChannelReportEntity(
    @PrimaryKey
    val id: String,
    val channelId: String,
    val channelName: String,
    val issueType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending"
) {
    fun toDomain(): LiveChannelReport = LiveChannelReport(
        id = id,
        channelId = channelId,
        channelName = channelName,
        issueType = issueType,
        description = description,
        timestamp = timestamp,
        status = status
    )

    companion object {
        fun fromDomain(report: LiveChannelReport): LiveChannelReportEntity = LiveChannelReportEntity(
            id = report.id,
            channelId = report.channelId,
            channelName = report.channelName,
            issueType = report.issueType,
            description = report.description,
            timestamp = report.timestamp,
            status = report.status
        )
    }
}
