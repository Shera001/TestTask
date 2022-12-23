package com.test.core.network

import com.test.core.model.Post
import retrofit2.http.*

interface PostsApiService {

    @GET(value = "posts")
    suspend fun getPosts(
        @Query("_start") page: Int = 1,
        @Query("_limit") pageSize: Int
    ): List<Post>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int)

    @FormUrlEncoded
    @POST("posts")
    suspend fun addPost(
        @Field("title") title: String,
        @Field("body") body: String,
        @Field("userId") userId: Int = 1
    ): Post

    @PUT("posts/{id}")
    suspend fun editPost(
        @Path("id") id: Int,
        @Body post: Post
    ): Post
}