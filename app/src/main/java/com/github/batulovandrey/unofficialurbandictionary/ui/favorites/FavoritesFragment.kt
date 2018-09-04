package com.github.batulovandrey.unofficialurbandictionary.ui.favorites

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.github.batulovandrey.unofficialurbandictionary.R
import com.github.batulovandrey.unofficialurbandictionary.UrbanDictionaryApp
import com.github.batulovandrey.unofficialurbandictionary.adapter.DefinitionAdapter
import com.github.batulovandrey.unofficialurbandictionary.ui.main.MainActivity
import javax.inject.Inject

class FavoritesFragment : Fragment(), FavoritesMvpView {

    @Inject
    lateinit var favoritesPresenter: FavoritesPresenter<FavoritesMvpView>

    private lateinit var favoritesDefinitionsRecyclerView: RecyclerView
    private lateinit var emptyFavTextView: TextView
    private lateinit var clearFavFAB: FloatingActionButton
    private lateinit var relativeLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UrbanDictionaryApp.getNetComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_list, container, false)
        favoritesDefinitionsRecyclerView = view.findViewById(R.id.definitions_recycler_view)
        emptyFavTextView = view.findViewById(R.id.empty_fav_text_view)
        clearFavFAB = view.findViewById(R.id.clear_favorites_action_button)
        relativeLayout = view.findViewById(R.id.relative_layout)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesPresenter.onAttach(this)
        clearFavFAB.setOnClickListener { showAlertDialog() }
    }

    override fun onResume() {
        super.onResume()
        favoritesPresenter.loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        favoritesPresenter.onDetach()
    }

    override fun showAlertDialog() {
        AlertDialog.Builder(context)
                .setTitle("Clear list of favorites")
                .setMessage("All items from favorite list will be removed. Are you sure?")
                .setPositiveButton("yes") { _, _ ->
                    favoritesPresenter.clearList()
                }
                .setNegativeButton("no") { dialog, _ -> dialog.dismiss() }
                .show()
    }

    override fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }

    override fun showData() {
        favoritesDefinitionsRecyclerView.visibility = View.VISIBLE
        emptyFavTextView.visibility = View.GONE
        relativeLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
    }

    override fun showPlaceHolder() {
        favoritesDefinitionsRecyclerView.visibility = View.GONE
        emptyFavTextView.visibility = View.VISIBLE
        relativeLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWhite))
    }

    override fun showDetailFragment() {
        (activity as MainActivity).showDetailFragment()
    }

    override fun setDefinitionAdapter(definitionAdapter: DefinitionAdapter) {
        favoritesDefinitionsRecyclerView.adapter = definitionAdapter
    }
}