package com.smartkdfarm.app.core.domain.model

/**
 * Centralised role + permission-level checker for Smart KD Farm.
 *
 * Two complementary layers:
 *  1. **Coarse role check** – ADMIN always passes every check.
 *     For non-ADMIN users a named role is compared against an allowed-list.
 *  2. **Fine-grained module check** – uses [StaffAccessProfile.permissions] to
 *     enforce VIEW / MANAGE / APPROVE distinctions per [StaffModule].
 *
 * Default module permissions per role are used as a safe fallback when no
 * explicit [StaffAccessProfile] has been created for the user yet.
 */
object PermissionChecker {

    // ── Coarse role checks ────────────────────────────────────────────────────

    /** Returns true when [user] has at least one of the [allowed] roles. ADMIN always passes. */
    fun hasRole(user: User, vararg allowed: UserRole): Boolean =
        user.role == UserRole.ADMIN || user.role in allowed

    /** Throws [AuthorizationException] when the user does not have an allowed role. */
    fun requireRole(user: User, vararg allowed: UserRole, message: String = "") {
        if (!hasRole(user, *allowed)) {
            val allowedNames = allowed.joinToString(" or ") { it.name }
            val msg = message.ifBlank { "Only $allowedNames can perform this action." }
            throw com.smartkdfarm.app.core.domain.exception.AuthorizationException(msg)
        }
    }

    // ── Fine-grained module permission checks ─────────────────────────────────

    /**
     * Returns true when [user] is permitted to perform [requiredLevel] on [module].
     *
     * Resolution order:
     *  1. ADMIN → always true.
     *  2. Explicit [staffPermissions] list, if provided.
     *  3. [defaultPermissions] for the user's role.
     *
     * PermissionLevel ordinal: NONE(0) < VIEW(1) < MANAGE(2) < APPROVE(3)
     */
    fun canAccess(
        user: User,
        module: StaffModule,
        requiredLevel: PermissionLevel,
        staffPermissions: List<StaffPermission> = emptyList(),
    ): Boolean {
        if (user.role == UserRole.ADMIN) return true

        val permissions = staffPermissions.ifEmpty { defaultPermissions(user.role) }
        val granted = permissions
            .firstOrNull { it.module == module }
            ?.level ?: PermissionLevel.NONE

        return granted.ordinal >= requiredLevel.ordinal
    }

    /** Same as [canAccess] but throws [AuthorizationException] on failure. */
    fun requireAccess(
        user: User,
        module: StaffModule,
        requiredLevel: PermissionLevel,
        staffPermissions: List<StaffPermission> = emptyList(),
    ) {
        if (!canAccess(user, module, requiredLevel, staffPermissions)) {
            throw com.smartkdfarm.app.core.domain.exception.AuthorizationException(
                "Insufficient permissions: ${requiredLevel.name} access to ${module.name} is required."
            )
        }
    }

    // ── Convenience helpers ───────────────────────────────────────────────────

    fun canView(user: User, module: StaffModule, staffPermissions: List<StaffPermission> = emptyList()) =
        canAccess(user, module, PermissionLevel.VIEW, staffPermissions)

    fun canManage(user: User, module: StaffModule, staffPermissions: List<StaffPermission> = emptyList()) =
        canAccess(user, module, PermissionLevel.MANAGE, staffPermissions)

    fun canApprove(user: User, module: StaffModule, staffPermissions: List<StaffPermission> = emptyList()) =
        canAccess(user, module, PermissionLevel.APPROVE, staffPermissions)

    // ── Default role → permissions map ────────────────────────────────────────

    /**
     * Baseline permissions assigned to each [UserRole] when no explicit
     * [StaffAccessProfile] exists.  These reflect the intended operational
     * responsibility of each role.
     *
     *  | Module        | ADMIN   | DAIRY_MAN | LABOUR  | FARMER |
     *  |---------------|---------|-----------|---------|--------|
     *  | DASHBOARD     | APPROVE | VIEW      | VIEW    | VIEW   |
     *  | LIVESTOCK     | APPROVE | MANAGE    | VIEW    | NONE   |
     *  | OUTER_CENTER  | APPROVE | MANAGE    | VIEW    | VIEW   |
     *  | KHATA         | APPROVE | VIEW      | NONE    | VIEW   |
     *  | INVENTORY     | APPROVE | VIEW      | MANAGE  | NONE   |
     *  | FARM_CONFIG   | APPROVE | NONE      | NONE    | NONE   |
     *  | STAFF_CONTROL | APPROVE | NONE      | NONE    | NONE   |
     *  | REPORTS       | APPROVE | VIEW      | NONE    | VIEW   |
     */
    fun defaultPermissions(role: UserRole): List<StaffPermission> = when (role) {
        UserRole.ADMIN -> StaffModule.entries.map { StaffPermission(it, PermissionLevel.APPROVE) }

        UserRole.DAIRY_MAN -> listOf(
            StaffPermission(StaffModule.DASHBOARD,      PermissionLevel.VIEW),
            StaffPermission(StaffModule.LIVESTOCK,      PermissionLevel.MANAGE),
            StaffPermission(StaffModule.OUTER_CENTER,   PermissionLevel.MANAGE),
            StaffPermission(StaffModule.KHATA,          PermissionLevel.VIEW),
            StaffPermission(StaffModule.INVENTORY,      PermissionLevel.VIEW),
            StaffPermission(StaffModule.FARM_CONFIG,    PermissionLevel.NONE),
            StaffPermission(StaffModule.STAFF_CONTROL,  PermissionLevel.NONE),
            StaffPermission(StaffModule.REPORTS,        PermissionLevel.VIEW),
        )

        UserRole.LABOUR -> listOf(
            StaffPermission(StaffModule.DASHBOARD,      PermissionLevel.VIEW),
            StaffPermission(StaffModule.LIVESTOCK,      PermissionLevel.VIEW),
            StaffPermission(StaffModule.OUTER_CENTER,   PermissionLevel.VIEW),
            StaffPermission(StaffModule.KHATA,          PermissionLevel.NONE),
            StaffPermission(StaffModule.INVENTORY,      PermissionLevel.MANAGE),
            StaffPermission(StaffModule.FARM_CONFIG,    PermissionLevel.NONE),
            StaffPermission(StaffModule.STAFF_CONTROL,  PermissionLevel.NONE),
            StaffPermission(StaffModule.REPORTS,        PermissionLevel.NONE),
        )

        UserRole.FARMER -> listOf(
            StaffPermission(StaffModule.DASHBOARD,      PermissionLevel.VIEW),
            StaffPermission(StaffModule.LIVESTOCK,      PermissionLevel.NONE),
            StaffPermission(StaffModule.OUTER_CENTER,   PermissionLevel.VIEW),
            StaffPermission(StaffModule.KHATA,          PermissionLevel.VIEW),
            StaffPermission(StaffModule.INVENTORY,      PermissionLevel.NONE),
            StaffPermission(StaffModule.FARM_CONFIG,    PermissionLevel.NONE),
            StaffPermission(StaffModule.STAFF_CONTROL,  PermissionLevel.NONE),
            StaffPermission(StaffModule.REPORTS,        PermissionLevel.VIEW),
        )
    }
}
