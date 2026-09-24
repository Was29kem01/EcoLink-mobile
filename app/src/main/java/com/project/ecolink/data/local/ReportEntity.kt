package com.project.ecolink.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey
    val localId: String = UUID.randomUUID().toString(),
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val priority: String = "Normal",
    val status: String = "Pending",
    val createdAt: Long = System.currentTimeMillis()
)
