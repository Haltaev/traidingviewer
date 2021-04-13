package com.traidingviewer.ui.home.viewpagers

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.traidingviewer.R
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.utils.ConvertUtils.dpToPx
import kotlinx.android.synthetic.main.item_home_list.view.*
import kotlinx.android.synthetic.main.view_progress.view.*

class HomePagesAdapter(
    private val items: List<HomeStock>,
    private val callback: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.apply {
            if (items[position].symbol.isNotBlank()) {
                if (position % 2 == 0) {
                    itemView.setCardBackgroundColor(
                        resources.getColor(
                            R.color.colorLightBlue,
                            null
                        )
                    )
                } else {
                    itemView.setCardBackgroundColor(resources.getColor(R.color.white, null))
                }
                if (!item.logo.isBlank()) {
                    Picasso.get().load(item.logo).into(homeListIcon)
                } else {
                    homeListIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.empty_image,
                            null
                        )
                    )
                }
                homeListName.text = item.name
                homeListTitle.text = item.symbol
                homeListPrice.text = resources.getString(R.string.usd_, item.currentPrice)
                homeListTitle.isActivated = item.isFavorite
                homeListPriceChanges.text =
                    resources.getString(R.string.price_, item.difference, item.percent)
                homeListPriceChanges.setTextColor(
                    if (item.difference.contains('-')) {
                        resources.getColor(R.color.colorRed, null)
                    } else {
                        resources.getColor(R.color.colorGreen, null)
                    }
                )
                itemView.setOnClickListener {
                    callback.onItemClickListener(item.symbol, item.name, item.isFavorite)
                }
                homeListTitle.setOnClickListener {
                    item.isFavorite = !item.isFavorite
                    notifyItemChanged(position)
                    callback.onFavoriteStarClickListener(item, item.isFavorite)
                }
            } else {
                progressBar.visibility = View.VISIBLE
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(26, context)
                ).apply {
                    gravity = Gravity.CENTER
                }
                progressBar.layoutParams = params
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TICKER_ITEM) {
            ViewHolder(
                layoutInflater.inflate(R.layout.item_home_list, parent, false)
            )
        } else {
            ProgressViewHolder(
                layoutInflater.inflate(R.layout.view_progress, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int =
        if (items[position].symbol.isBlank()) PROGRESS_ITEM else TICKER_ITEM

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val PROGRESS_ITEM = 1
        const val TICKER_ITEM = 0
    }
}