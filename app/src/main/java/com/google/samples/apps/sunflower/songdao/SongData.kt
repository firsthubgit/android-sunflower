package com.google.samples.apps.sunflower.songdao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "down_song")
data class DownSong(
        @PrimaryKey (autoGenerate = true)
        val _id : Long,
        val songid : String,
        val songname : String,
        val albumname : String,
        val songmid : String,
        val albummid : String,
        //表示插入Media库的id
        @ColumnInfo(name = "track_id")
        val trackId : String
){
    override fun toString() = "$songid:$songname"
}

@Entity(tableName = "recent_song")
data class RecentSong(
        @PrimaryKey
        @ColumnInfo(name = "track_id")
        val trackId : String,
        @ColumnInfo(name = "palyed_time")
        val palyedTime : String){
}

@Entity(tableName = "songlist_info")
data class SongListInfo(
        @PrimaryKey
        @ColumnInfo(name = "playlist_id")
        val playlistId : Long,
        @ColumnInfo(name = "palylist_name")
        val playlistName : String,
        @ColumnInfo(name = "album_art")
        val albumArt : String
)

@Entity(tableName = "songlists")
data class SongLists(
        @ColumnInfo(name = "playlist_id")
        val playlistId : Long,
        @ColumnInfo(name = "track_id")
        val trackId : Long
)





data class Singer(
        val id : Int,
        val mid : String,
        val name : String,
        val name_hilight : String
) : Serializable

data class QQSong(
        val songid : String,
        val songname : String,
        val lyric : String,
        val lyric_hilight : String,
        //时间长度
        val interval : String,
        val albumname : String,
        val songmid : String,
        val albummid : String,
        val singer : List<Singer>
) : Serializable