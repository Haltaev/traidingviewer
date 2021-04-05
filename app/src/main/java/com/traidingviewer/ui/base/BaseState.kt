package com.traidingviewer.ui.base

sealed class BaseState<T> {
    class Success<T>(val body: T?) : BaseState<T>()
    sealed class Failure<T> : BaseState<T>() {
        class LimitExceeded<T> : Failure<T>()
        class UnknownHostException<T> : Failure<T>()
        class OtherError<T> : Failure<T>()
    }
}