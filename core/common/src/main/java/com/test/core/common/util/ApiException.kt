package com.test.core.common.util

class ApiException<T : Any>(val e: Throwable) : ApiResult<T>