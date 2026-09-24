package com.project.ecolink.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM reports ORDER BY createdAt ASC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>
    
    @Query("SELECT * FROM reports ORDER BY createdAt ASC")
    suspend fun getAllReportsSync(): List<ReportEntity>

    @Delete
    suspend fun deleteReport(report: ReportEntity)
    
    @Query("DELETE FROM reports WHERE localId = :id")
    suspend fun deleteReportById(id: String)
}
