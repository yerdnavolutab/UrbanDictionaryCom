package com.github.batulovandrey.unofficialurbandictionary.ui.main

import com.github.batulovandrey.unofficialurbandictionary.presenter.MvpPresenter

interface MainMvpPresenter<V: MainMvpView>: MvpPresenter<V> {

    fun onViewInitialized()

    fun showSearch()

    fun showPopularWords()

    fun showFavorites()

    fun showDetail()
}