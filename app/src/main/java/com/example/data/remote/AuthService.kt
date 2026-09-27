package com.example.data.remote

import com.example.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class AdminUser(
    val name: String,
    val email: String,
    val role: UserRole
)

data class AdminSession(
    val isAuthenticated: Boolean = false,
    val token: String? = null,
    val userRole: UserRole = UserRole.USER,
    val adminName: String = "",
    val adminEmail: String = ""
) {
    val role: UserRole get() = userRole
    val name: String get() = adminName.ifBlank { "Admin" }
    val email: String get() = adminEmail
    val user: AdminUser get() = AdminUser(name = name, email = email, role = userRole)
}

class AuthService {

    private val _currentSession = MutableStateFlow(
        AdminSession(
            isAuthenticated = true,
            userRole = UserRole.SUPER_ADMIN,
            adminName = "Super Admin (Default)",
            adminEmail = "superadmin@z1movies.com",
            token = "jwt_z1_root_init"
        )
    )
    val currentSession: StateFlow<AdminSession> = _currentSession.asStateFlow()

    // Pre-registered admin credentials for testing / default setup
    private val adminCredentials = mapOf(
        "superadmin@z1movies.com" to Pair("superadmin123", UserRole.SUPER_ADMIN),
        "admin@z1movies.com" to Pair("admin123", UserRole.ADMIN),
        "editor@z1movies.com" to Pair("editor123", UserRole.EDITOR),
        "user@z1movies.com" to Pair("user123", UserRole.USER),
        "superadmin@z1movies.stream" to Pair("SuperAdmin2026!", UserRole.SUPER_ADMIN),
        "admin@z1movies.stream" to Pair("AdminZ1Pass!", UserRole.ADMIN),
        "editor@z1movies.stream" to Pair("EditorZ1Pass!", UserRole.EDITOR),
        // PIN based logins
        "superadmin" to Pair("1234", UserRole.SUPER_ADMIN),
        "admin" to Pair("1234", UserRole.ADMIN),
        "editor" to Pair("1234", UserRole.EDITOR)
    )

    fun login(identifier: String, secretOrPin: String): Result<AdminSession> {
        val trimmedIdentifier = identifier.trim().lowercase()
        val trimmedSecret = secretOrPin.trim()

        val credential = adminCredentials[trimmedIdentifier]
        if (credential != null && (credential.first == trimmedSecret || trimmedSecret == "1234" || trimmedSecret == "admin123")) {
            val session = AdminSession(
                isAuthenticated = true,
                token = "jwt_z1_${UUID.randomUUID()}",
                userRole = credential.second,
                adminName = when (credential.second) {
                    UserRole.SUPER_ADMIN -> "Lead Studio Director (Super Admin)"
                    UserRole.ADMIN -> "Z1 Content Administrator"
                    UserRole.EDITOR -> "Movie Catalog Editor"
                    UserRole.USER -> "Standard User"
                },
                adminEmail = trimmedIdentifier
            )
            _currentSession.value = session
            return Result.success(session)
        }

        // Allow entering ANY identifier if entering master demo key "z1admin", "1234", or "admin123"
        if (trimmedSecret == "z1admin" || trimmedSecret == "1234" || trimmedSecret == "admin123") {
            val session = AdminSession(
                isAuthenticated = true,
                token = "jwt_z1_${UUID.randomUUID()}",
                userRole = UserRole.ADMIN,
                adminName = trimmedIdentifier.substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank { "Admin" },
                adminEmail = if (trimmedIdentifier.contains("@")) trimmedIdentifier else "$trimmedIdentifier@z1movies.com"
            )
            _currentSession.value = session
            return Result.success(session)
        }

        return Result.failure(Exception("Invalid admin credentials. Use admin@z1movies.com / admin123 or select a role."))
    }

    fun loginAdmin(identifier: String, secretOrPin: String): Result<AdminSession> = login(identifier, secretOrPin)

    fun logout() {
        _currentSession.value = AdminSession(isAuthenticated = false)
    }

    fun logoutAdmin() = logout()

    fun switchRole(role: UserRole) {
        _currentSession.value = AdminSession(
            isAuthenticated = true,
            token = "jwt_z1_${UUID.randomUUID()}",
            userRole = role,
            adminName = when (role) {
                UserRole.SUPER_ADMIN -> "Lead Studio Director (Super Admin)"
                UserRole.ADMIN -> "Z1 Content Administrator"
                UserRole.EDITOR -> "Movie Catalog Editor"
                UserRole.USER -> "Standard User"
            },
            adminEmail = "${role.name.lowercase()}@z1movies.com"
        )
    }

    fun canPerform(action: (UserRole) -> Boolean): Boolean {
        val role = _currentSession.value.userRole
        return _currentSession.value.isAuthenticated && action(role)
    }
}
