package com.traidingviewer.ui.info.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.traidingviewer.R
import com.traidingviewer.data.api.model.CompanyNewsResponse
import kotlinx.android.synthetic.main.item_news.view.*
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(
    private val items: List<CompanyNewsResponse>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.apply {
            newsBody.changeVisibility(item.isPicked)
            newsLink.changeVisibility(item.isPicked)
            if (item.isPicked) {
                newsIconMore.setImageResource(R.drawable.ic_arrow_up)
            } else {
                newsIconMore.setImageResource(R.drawable.ic_arrow_down)
            }
            newsSource.text = item.site
            newsDate.text = item.publishedDate.toLocalDateFormat()
            if (!item.image.isBlank()) {
                Picasso.get().load(item.image).into(newsImage)
                newsImage.visibility = View.VISIBLE
            } else newsImage.visibility = View.GONE
            newsTitle.text = item.title
            newsBody.text = item.text
            newsLink.text = item.url
            newsLayout.setOnClickListener {
                item.isPicked = !item.isPicked
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_news,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun View.changeVisibility(isVisible: Boolean) {
        if (isVisible) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }

    private fun String.toLocalDateFormat(): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("us"))
        val outputDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("us"))
        val date = inputDateFormat.parse(this)
        return if (date != null) outputDateFormat.format(date) else ""
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view = itemView
    }
}