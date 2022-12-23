package com.test.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.core.database.dao.PostDao
import com.test.core.database.entity.DeletedPostEntity
import com.test.core.database.entity.PostEntity

@Database(
    entities = [PostEntity::class, DeletedPostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}