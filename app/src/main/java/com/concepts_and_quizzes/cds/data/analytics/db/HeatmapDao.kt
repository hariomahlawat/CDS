package com.concepts_and_quizzes.cds.data.analytics.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeatmapDao {

    /** Per-day minutes since cutoff, with weekday [0=Sun..6=Sat] and yearWeek key "YYYYWW". */
    @Query(
        """
        SELECT 
            date(timestamp/1000,'unixepoch','localtime') AS day,
            CAST(strftime('%w', timestamp/1000,'unixepoch','localtime') AS INTEGER) AS dow,
            CAST(strftime('%Y%W', timestamp/1000,'unixepoch','localtime') AS INTEGER) AS yw,
            SUM(durationMs)/60000.0 AS minutes
        FROM attempt_log
        WHERE timestamp >= :cutoff
        GROUP BY day
        ORDER BY day ASC
        """
    )
    fun dailyHeatmapSince(cutoff: Long): Flow<List<DailyHeatDb>>

    /** Per hour (0..23) × weekday minutes since cutoff. */
    @Query(
        """
        SELECT 
            CAST(strftime('%w', timestamp/1000,'unixepoch','localtime') AS INTEGER) AS dow,
            CAST(strftime('%H', timestamp/1000,'unixepoch','localtime') AS INTEGER) AS hour,
            SUM(durationMs)/60000.0 AS minutes
        FROM attempt_log
        WHERE timestamp >= :cutoff
        GROUP BY dow, hour
        ORDER BY dow, hour
        """
    )
    fun hourlyHeatmapSince(cutoff: Long): Flow<List<HourlyHeatDb>>
}

/* DTOs */
data class DailyHeatDb(
    val day: String,     // "YYYY-MM-DD"
    val dow: Int,        // 0..6 (Sun..Sat)
    val yw: Int,         // YYYYWW (e.g., 202435)
    val minutes: Double
)

data class HourlyHeatDb(
    val dow: Int,        // 0..6 (Sun..Sat)
    val hour: Int,       // 0..23
    val minutes: Double
)
