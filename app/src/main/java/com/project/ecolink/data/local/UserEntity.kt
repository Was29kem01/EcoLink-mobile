package com.project.ecolink.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String, // "CLIENT" or "FIELD_AGENT"
    val passwordHash: String,
    val branchCode: String? = null
)
