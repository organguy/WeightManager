package kr.co.weightmanager.interfaces

interface OnResultListener<T> {
    fun onSuccess(result: T)
    fun onFail()
}