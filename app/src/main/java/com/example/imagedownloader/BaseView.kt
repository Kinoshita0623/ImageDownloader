package com.example.imagedownloader

interface BaseView<T: BasePresenter> {
    fun setPresenter(presenter: T)
}