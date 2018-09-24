package com.github.batulovandrey.unofficialurbandictionary.ui.main

import com.crashlytics.android.Crashlytics
import com.github.batulovandrey.unofficialurbandictionary.R
import com.github.batulovandrey.unofficialurbandictionary.adapter.DefinitionAdapter
import com.github.batulovandrey.unofficialurbandictionary.adapter.DefinitionClickListener
import com.github.batulovandrey.unofficialurbandictionary.adapter.QueriesAdapter
import com.github.batulovandrey.unofficialurbandictionary.adapter.QueriesClickListener
import com.github.batulovandrey.unofficialurbandictionary.data.DataManager
import com.github.batulovandrey.unofficialurbandictionary.data.bean.BaseResponse
import com.github.batulovandrey.unofficialurbandictionary.data.db.model.SavedUserQuery
import com.github.batulovandrey.unofficialurbandictionary.presenter.BasePresenter
import com.github.batulovandrey.unofficialurbandictionary.ui.ADS_COUNT
import com.github.batulovandrey.unofficialurbandictionary.utils.convertToDefinitionList
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainPresenter<V : MainMvpView> @Inject constructor(dataManager: DataManager,
                                                         compositeDisposable: CompositeDisposable) :
        BasePresenter<V>(dataManager, compositeDisposable),
        MainMvpPresenter<V>,
        DefinitionClickListener,
        QueriesClickListener {

    private var definitionAdapter: DefinitionAdapter? = null
    private var queriesAdapter: QueriesAdapter? = null

    override fun onAttach(mvpView: V) {
        super.onAttach(mvpView)
        if (dataManager.getSavedListOfDefinition().isNotEmpty()) {
            definitionAdapter = DefinitionAdapter(dataManager.getSavedListOfDefinition(), this)
            mvpView.setDefinitionAdapter(definitionAdapter!!)
            mvpView.showDefinitions()
        }
    }

    override fun onViewInitialized() {
        dataManager.getDefinitions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isViewAttached()) {

                        definitionAdapter = DefinitionAdapter(it, this)
                        definitionAdapter?.let { adapter -> mvpView?.setDefinitionAdapter(adapter) }
                        mvpView?.showDefinitions()
                        mvpView?.hideKeyboard()

                    } else {

                        Crashlytics.log("view is not attached")
                        return@subscribe
                    }
                },
                        {
                            Crashlytics.log(it.message)
                            mvpView?.hideKeyboard()
                            mvpView?.showQueries()
                        })?.let { compositeDisposable.add(it) }
    }

    override fun getData(text: String) {
        loadData(dataManager.getData(text))
    }

    override fun getRandom() {
        loadData(dataManager.getRandom())
    }

    override fun showData() {
        if (definitionAdapter != null) {
            mvpView?.hideLoading()
            mvpView?.hideKeyboard()

            mvpView?.showDefinitions()
        } else {
            getRandom()
        }
    }

    override fun saveUserQuery(query: String) {
        compositeDisposable.add(dataManager.getAllQueries()
                .subscribeOn(Schedulers.io())
                .flatMapIterable { it }
                .filter { it.text.toLowerCase() == query.toLowerCase() }
                .toList()
                .toObservable()
                .subscribe({ list ->
                    if (list.isEmpty()) {
                        dataManager.saveQuery(SavedUserQuery(null, query))
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                    }
                },
                        {
                            Crashlytics.log(it.message)
                        })
        )
    }

    override fun showSearch() {
        mvpView?.closeNavigationDrawer()
        mvpView?.showSearchFragment()
    }

    override fun showPopularWords() {
        mvpView?.closeNavigationDrawer()
        mvpView?.showPopularWordsFragment()
    }

    override fun showFavorites() {
        mvpView?.closeNavigationDrawer()
        mvpView?.showFavoritesFragment()
    }

    override fun showDetail() {
        mvpView?.closeNavigationDrawer()
        mvpView?.showDetailFragment()
    }

    override fun filterQueries(text: String) {
        compositeDisposable.add(dataManager.getAllQueries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable { it }
                .filter { it.text.toLowerCase().contains(text.toLowerCase()) }
                .toList()
                .toObservable()
                .subscribe({
                    if (isViewAttached()) {

                        queriesAdapter = QueriesAdapter(it, this)
                        queriesAdapter?.let { adapter -> mvpView?.setQueriesAdapter(adapter) }
                        mvpView?.showQueries()

                    } else {

                        return@subscribe

                    }
                },
                        {
                            Crashlytics.log(it.message)
                        }))
    }

    override fun onItemClick(position: Int) {
        var selectDefinition = dataManager.getSavedListOfDefinition()[position]

        compositeDisposable.add(dataManager.getDefinitions()
                .subscribeOn(Schedulers.io())
                .map { list ->
                    val definition = list.findLast { item -> item == selectDefinition }
                    definition?.let {
                        selectDefinition = definition
                        dataManager.setActiveDefinition(selectDefinition)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mvpView?.showDetailFragment()
                },
                        {
                            Crashlytics.log(it.message)
                        }))
    }

    override fun onQueryClick(position: Int) {
        val userQuery = queriesAdapter?.getQuery(position)
        val text = userQuery?.text
        text?.let {
            getData(text)
        }
    }

    override fun deleteQueryFromRealm(position: Int) {
        val userQuery = queriesAdapter?.getQuery(position)
        userQuery?.let { query ->
            compositeDisposable.add(
                    dataManager.deleteQuery(query)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                queriesAdapter?.removeQuery(position)
                                mvpView?.setQueriesAdapter(queriesAdapter!!)
                            },
                                    {
                                        Crashlytics.log(it.message)
                                    }))
        }
    }

    private fun loadData(single: Single<BaseResponse>) {
        ADS_COUNT.incrementAndGet()
        dataManager.clearMap()
        compositeDisposable.clear()
        mvpView?.showLoading()

        compositeDisposable.add(single
                .flatMap {
                    Single.fromCallable { it.definitionResponses }
                }
                .toObservable()
                .flatMap { Observable.fromArray(it.convertToDefinitionList()) }
                .map { list ->
                    list.forEach { definition ->
                        dataManager.getDefinitions()
                                .subscribe {
                                    if (it.contains(definition)) {
                                        dataManager.putDefinitionToSavedList(definition)
                                    } else {
                                        compositeDisposable.add(dataManager.saveDefinition(definition)
                                                .subscribe { _ ->
                                                    dataManager.putDefinitionToSavedList(definition)
                                                })
                                    }
                                }
                    }

                    definitionAdapter = DefinitionAdapter(dataManager.getSavedListOfDefinition(), this)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    mvpView?.hideLoading()
                    mvpView?.hideKeyboard()

                    definitionAdapter?.let { adapter ->
                        adapter.notifyDataSetChanged()
                        mvpView?.setDefinitionAdapter(adapter)
                    }
                    mvpView?.showDefinitions()
                },
                        {
                            Crashlytics.log("error load data")
                            mvpView?.showToast(R.string.error)
                            mvpView?.hideKeyboard()
                            mvpView?.hideLoading()
                            mvpView?.showSnackbar()
                        }))
    }
}