package dreamcatcher.howaboutit.features.feed

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.ItemEntity
import kotlinx.android.synthetic.main.grid_single_item.view.*
import kotlinx.android.synthetic.main.two_items_row.view.*

class GeneralListAdapter (private val clickListener: (String) -> Unit) : RecyclerView.Adapter<GeneralListAdapter.ViewHolder>() {

    private var itemsList: List<List<ItemEntity>> = ArrayList()
    private var context: Context? = null

    companion object {
        internal val VIEW_TYPE_TWO_ITEMS_ROW = 0
        internal val VIEW_TYPE_TWO_ITEMS_WITH_PROTIP_ROW = 1
    }

    fun setItems(articles: List<ItemEntity>) {
        this.itemsList = convertArticlesListIntoClustersList(articles)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun getItemViewType(position: Int): Int {
        // Return 0 or 1 per each position to indicate viewtype.
        return position % 2
        //return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var viewHolder: ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {

            VIEW_TYPE_TWO_ITEMS_ROW -> {
                val columnView = inflater
                    .inflate(R.layout.two_items_row, parent, false)
                viewHolder = twoItemsRowViewHolder(columnView)
            }

            VIEW_TYPE_TWO_ITEMS_WITH_PROTIP_ROW -> {
                val viewPagerView = inflater
                    .inflate(R.layout.two_items_with_protip_row, parent, false)
                viewHolder = twoItemsRowViewHolder(viewPagerView)
            }
        }

        return viewHolder!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (holder.itemViewType) {

            VIEW_TYPE_TWO_ITEMS_ROW -> {
                val twoItemsRowViewHolder = holder as twoItemsRowViewHolder
                configureTwoItemsRowView(twoItemsRowViewHolder, position)
            }

            VIEW_TYPE_TWO_ITEMS_WITH_PROTIP_ROW -> {
                val twoItemsRowViewHolder = holder as twoItemsRowViewHolder
                configureTwoItemsRowView(twoItemsRowViewHolder, position)
            }
        }
    }

    private fun configureRowView(holder: RowViewHolder, position: Int) {
        /*for ((index, article) in holder.articleViews.withIndex()) {
            try {
                // Prepare fetched data
                val title = articlesList[position][index].title
                val summary = articlesList[position][index].summary
                val thumbnailUrl = articlesList[position][index].thumbnailUrl

                // Set data within the holder
                article.textView_title.text = title
                article.textView_summary.text = summary

                // Load thumbnail
                Picasso.with(context).load(thumbnailUrl).into(article.thumbnail)

                // Set onClickListener
                article.setOnClickListener{
                    val articleId = articlesList[position][index].id
                    clickListener(articleId)
                }

            } catch (e: Exception) {
                Log.e("Exception", e.message)
            }
        }*/
    }

    private fun configureTwoItemsRowView(holder: twoItemsRowViewHolder, position: Int) {

        for (i in 0..1) {
            
            try {

                // Select an item
                val item = itemsList[position][i]

                // Select view
                val view = holder.views[i]

                // Prepare fetched data
                val name = item.name
                val imageLink = item.imageLink

                // Set data within the holder
                view.name.text = name

                // Load thumbnail
                Picasso.with(context).load(imageLink).into(view.thumbnail)

                // Set onClickListener
                view.setOnClickListener{
                    val itemId = item.id
                    clickListener(itemId)
                }
                
            } catch(e: Exception) {
                Log.e("Exception", e.message);
            }
        }
    }

    abstract class ViewHolder (view: View) : RecyclerView.ViewHolder(view)

    inner class twoItemsRowViewHolder (view: View) : ViewHolder(view) {
        val views = ArrayList<View>()

        init {
            views.add(view.left_item)
            views.add(view.right_item)
        }
    }

    inner class RowViewHolder (view: View) : ViewHolder(view) {
        //val viewPager = view.viewpager_triplet_container
    }

    // Converter grouping items together into 2-items clusters
    private fun convertArticlesListIntoClustersList(itemsList: List<ItemEntity>)
            : List<List<ItemEntity>> {
        val clustersList = ArrayList<List<ItemEntity>>()
        itemsList.chunked(2).forEach {
            clustersList.add(it)
        }
        return clustersList
    }
}