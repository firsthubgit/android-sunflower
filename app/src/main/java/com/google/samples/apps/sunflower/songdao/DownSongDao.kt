package com.google.samples.apps.sunflower.songdao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.samples.apps.sunflower.data.Plant

/**
 * The Data Access Object for the Plant class.
 */
@Dao
interface DownSongDao {

    @Query("SELECT * FROM down_song")
    fun getDownList(): LiveData<List<DownSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song : DownSong)



//    @Query("SELECT * FROM down_song WHERE growZoneNumber = :growZoneNumber ORDER BY name")
//    fun getPlantsWithGrowZoneNumber(growZoneNumber: Int): LiveData<List<Plant>>
//
//    @Query("SELECT * FROM down_song WHERE id = :plantId")
//    fun getPlant(plantId: String): LiveData<Plant>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(plants: List<Plant>)
}