package com.example.data.local.converters

import androidx.room.TypeConverter
import com.example.data.model.DownloadStatus
import com.example.data.model.NotificationType
import com.example.data.model.UserRole
import org.json.JSONArray

class Z1TypeConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String> {
        if (jsonString.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            // fallback: split by comma
            return jsonString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
        return list
    }

    @TypeConverter
    fun fromUserRole(role: UserRole?): String = role?.name ?: UserRole.USER.name

    @TypeConverter
    fun toUserRole(name: String?): UserRole {
        return try {
            if (name != null) UserRole.valueOf(name) else UserRole.USER
        } catch (e: Exception) {
            UserRole.USER
        }
    }

    @TypeConverter
    fun fromNotificationType(type: NotificationType?): String = type?.name ?: NotificationType.NEW_RELEASE.name

    @TypeConverter
    fun toNotificationType(name: String?): NotificationType {
        return try {
            if (name != null) NotificationType.valueOf(name) else NotificationType.NEW_RELEASE
        } catch (e: Exception) {
            NotificationType.NEW_RELEASE
        }
    }

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus?): String = status?.name ?: DownloadStatus.COMPLETED.name

    @TypeConverter
    fun toDownloadStatus(name: String?): DownloadStatus {
        return try {
            if (name != null) DownloadStatus.valueOf(name) else DownloadStatus.COMPLETED
        } catch (e: Exception) {
            DownloadStatus.COMPLETED
        }
    }

    @TypeConverter
    fun fromEpgProgramList(list: List<com.example.data.model.EpgProgram>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { prog ->
            val obj = org.json.JSONObject()
            obj.put("id", prog.id)
            obj.put("title", prog.title)
            obj.put("description", prog.description)
            obj.put("startTime", prog.startTime)
            obj.put("endTime", prog.endTime)
            obj.put("isLiveNow", prog.isLiveNow)
            obj.put("category", prog.category)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toEpgProgramList(json: String?): List<com.example.data.model.EpgProgram> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<com.example.data.model.EpgProgram>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    com.example.data.model.EpgProgram(
                        id = obj.optString("id", ""),
                        title = obj.optString("title", ""),
                        description = obj.optString("description", ""),
                        startTime = obj.optString("startTime", ""),
                        endTime = obj.optString("endTime", ""),
                        isLiveNow = obj.optBoolean("isLiveNow", false),
                        category = obj.optString("category", "General")
                    )
                )
            }
        } catch (e: Exception) {
            // return empty on parse error
        }
        return list
    }
}
