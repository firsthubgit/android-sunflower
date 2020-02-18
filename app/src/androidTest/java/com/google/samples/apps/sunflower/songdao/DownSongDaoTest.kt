package com.google.samples.apps.sunflower.songdao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantDao
import com.google.samples.apps.sunflower.utilities.getValue
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownSongDaoTest {
    private lateinit var database: SongDatabase
    private lateinit var downSongDao: DownSongDao
    private val songA = DownSong(4,"1111" ,"name1", "专辑1", "songmid", "albumid","trackId" )
    private val songB = DownSong(0,"2222" ,"name2", "专辑1", "songmid", "albumid","trackId")
    private val songC = DownSong(0,"3333" ,"name3", "专辑1", "songmid", "albumid","trackId")
    private val songD = DownSong(6,"6666" ,"name6", "专辑6", "songmid6", "albumid","trackId")

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SongDatabase::class.java).build()
        downSongDao = database.getDownSongDao()

        // Insert plants in non-alphabetical order to test that results are sorted by name
        downSongDao.insert(songA)
        downSongDao.insert(songB)
        downSongDao.insert(songC)
        downSongDao.insert(songD)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetDownSongs() {
        val plantList = getValue(downSongDao.getDownList())
        Assert.assertThat(plantList.size, Matchers.equalTo(3))

        print("message" + plantList[2]._id.toString())
        // Ensure plant list is sorted by name
         Assert.assertThat(plantList[0]._id.toInt(), Matchers.equalTo(2))
        Assert.assertThat(plantList[1]._id.toInt(), Matchers.equalTo(2))
        Assert.assertThat(plantList[2]._id.toInt(), Matchers.equalTo(3))
    }

    @Test
    fun testGetPlantsWithGrowZoneNumber() {
//        val plantList = getValue(plantDao.getPlantsWithGrowZoneNumber(1))
//        Assert.assertThat(plantList.size, Matchers.equalTo(2))
//        Assert.assertThat(getValue(plantDao.getPlantsWithGrowZoneNumber(2)).size, Matchers.equalTo(1))
//        Assert.assertThat(getValue(plantDao.getPlantsWithGrowZoneNumber(3)).size, Matchers.equalTo(0))
//
//        // Ensure plant list is sorted by name
//        Assert.assertThat(plantList[0], Matchers.equalTo(plantA))
//        Assert.assertThat(plantList[1], Matchers.equalTo(plantB))
    }

    @Test
    fun testGetPlant() {
//        Assert.assertThat(getValue(plantDao.getPlant(plantA.plantId)), Matchers.equalTo(plantA))
    }
}