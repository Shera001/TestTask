package com.test.core.network.utils

import com.test.core.common.util.ApiError
import com.test.core.common.util.ApiException
import com.test.core.common.util.ApiResult
import com.test.core.common.util.ApiSuccess
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class NetworkResultCall<T : Any>(
    private val proxy: Call<T>
) : Call<ApiResult<T>> {

    override fun enqueue(callback: Callback<ApiResult<T>>) {
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val apiResult = handelApi { response }
                callback.onResponse(this@NetworkResultCall, Response.success(apiResult))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val apiResult = ApiException<T>(t)
                callback.onResponse(this@NetworkResultCall, Response.success(apiResult))
            }
        })
    }


    override fun isExecuted(): Boolean = proxy.isExecuted

    override fun cancel() {
        proxy.cancel()
    }

    override fun isCanceled(): Boolean = proxy.isCanceled

    override fun request(): Request = proxy.request()

    override fun timeout(): Timeout = proxy.timeout()

    override fun clone(): Call<ApiResult<T>> = NetworkResultCall(proxy.clone())

    override fun execute(): Response<ApiResult<T>> = throw NotImplementedError()

    private fun <R : Any> handelApi(
        execute: () -> Response<R>
    ): ApiResult<R> {
        return try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && (body != null)) {
                ApiSuccess(body)
            } else {
                ApiError(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: HttpException) {
            ApiError(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            ApiException(e)
        }
    }
}