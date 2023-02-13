package com.test.core.common.util

class ApiError<T : Any>(val code: Int, val message: String) : ApiResult<T>