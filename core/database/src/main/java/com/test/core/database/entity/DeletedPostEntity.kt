package com.test.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_post")
data class DeletedPostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "defaultId")
    val defaultId: Int? = null,
    @ColumnInfo(name = "id")
    val id: Int
)
