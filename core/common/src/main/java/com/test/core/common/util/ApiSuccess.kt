package com.test.core.common.util

class ApiSuccess<T : Any>(val data: T) : ApiResult<T>