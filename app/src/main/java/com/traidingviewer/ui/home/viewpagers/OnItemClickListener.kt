package com.traidingviewer.ui.home.viewpagers

import com.traidingviewer.data.api.model.HomeStock

interface OnItemClickListener {
    fun onItemClickListener(symbol: String, name: String, isFavorite: Boolean)
    fun onFavoriteStarClickListener(item: HomeStock, state: Boolean)
}