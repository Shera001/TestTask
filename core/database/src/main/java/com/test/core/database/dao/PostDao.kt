package com.test.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.test.core.database.entity.DeletedPostEntity
import com.test.core.database.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM posts")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Insert(onConflict = REPLACE)
    suspend fun insert(post: PostEntity)

    @Update
    suspend fun update(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun clear()

    @Query("DELETE FROM posts WHERE id=:id")
    suspend fun deletePostById(id: Int)

    @Transaction
    suspend fun refresh(posts: List<PostEntity>) {
        clear()
        insertAll(posts)
    }

    @Insert(onConflict = REPLACE)
    suspend fun addDeletedPost(deletedPostEntity: DeletedPostEntity)

    @Query("SELECT * FROM `deleted_post`")
    suspend fun getAllDeletedPosts(): List<DeletedPostEntity>

    @Query("DELETE FROM `deleted_post` WHERE id=:id")
    suspend fun clearDeletedPostById(id: Int)
}