package dreamcatcher.howaboutit.features.feed

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.items.ItemEntity
import dreamcatcher.howaboutit.data.database.protips.ProtipEntity
import kotlinx.android.synthetic.main.grid_single_item.view.*
import kotlinx.android.synthetic.main.horizontal_row_view.view.*
import kotlinx.android.synthetic.main.two_items_row.view.left_item
import kotlinx.android.synthetic.main.two_items_row.view.right_item
import kotlinx.android.synthetic.main.two_items_with_protip_row.view.*

class GeneralListAdapter (private val clickListener: (String) -> Unit) : RecyclerView.Adapter<GeneralListAdapter.ViewHolder>() {

    private var itemsList: List<List<ItemEntity>> = ArrayList()
    private var protipsList: List<ProtipEntity> = ArrayList()
    private var usedProtipsList: List<ProtipEntity> = ArrayList()
    private var context: Context? = null

    companion object {
        internal val VIEW_TYPE_TWO_ITEMS_ROW = 0
        internal val VIEW_TYPE_TWO_ITEMS_WITH_PROTIP_ROW = 1
    }

    fun updateItems(items: List<ItemEntity>) {
        this.itemsList = convertSingleItemsListIntoClustersList(items)
        notifyDataSetChanged()
    }

    fun setItemsAndProtips(items: List<ItemEntity>, protips: List<ProtipEntity>) {
        this.itemsList = convertSingleItemsListIntoClustersList(items)
        this.protipsList = protips
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun getItemViewType(position: Int): Int {
        // Return 0 or 1 per each position to indicate viewtype.
        return position % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var viewHolder: ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {

            VIEW_TYPE_TWO_ITEMS_ROW -> {
                val twoItemsRowView = inflater
                    .inflate(R.layout.two_items_row, parent, false)
                viewHolder = TwoItemsRowViewHolder(twoItemsRowView)
            }

            VIEW_TYPE_TWO_ITEMS_WITH_PROTIP_ROW -> {
                val twoItemsWithProtipRowView = inflater
                    .inflate(R.layout.two_items_with_protip_row, parent, false)
                viewHolder = TwoItemsWithProtipRowViewHolder(twoItemsWithProtipRowView)
            }
        }

        return viewHolder!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (holder.itemViewType) {

            VIEW_TYPE_TWO_ITEMS_ROW -> {
                val twoItemsRowViewHolder = holder as TwoItemsRowViewHolder
                configureTwoItemsRowView(twoItemsRowViewHolder, position)
            }

            VIEW_TYPE_TWO_ITEMS_WITH_PROTIP_ROW -> {
                val twoItemsWithProtipRowViewHolder = holder as TwoItemsWithProtipRowViewHolder
                configureTwoItemsWithProtipRowView(twoItemsWithProtipRowViewHolder, position)
            }
        }
    }

    private fun configureTwoItemsRowView(holder: TwoItemsRowViewHolder, position: Int) {

        // Set two items views - left and right
        configureLeftAndRightItemsViews(holder, position)
    }

    private fun configureTwoItemsWithProtipRowView(holder: TwoItemsWithProtipRowViewHolder, position: Int) {

        // Set two items views - left and right
        configureLeftAndRightItemsViews(holder, position)

        // Set protip view
        configureProtipView(holder, position)
    }

    private fun configureLeftAndRightItemsViews(holder: TwoItemsRowViewHolder, position: Int) {

        var view: View? = null
        var item: ItemEntity? = null

        for (i in 0..1) {

            // Clear variables
            view = null
            item = null

            try {

                // Select view
                view = holder.views[i]

                // Ensure the view is visible (it could be hidden before)
                view.visibility = View.VISIBLE

                // Select an item
                item = itemsList[position][i]

                // Prepare fetched data
                val name = item.name
                var imageLink = item.imageLink

                // Set data within the holder
                view.name.text = name

                // Set onClickListener
                val itemId = item?.id
                view.setOnClickListener{
                    clickListener(itemId)
                }

                // Order the image in smaller size to provide fluent loading.
                imageLink += "?auto=compress&cs=tinysrgb&dpr=1&h=300&w=300"

                // Load thumbnail
                if (!imageLink.isNullOrEmpty()) {
                    Picasso.get().load(imageLink).into(view.thumbnail)
                } else {
                    view.thumbnail.setImageDrawable(null);
                }

            } catch(e: Exception) {
                Log.e("Exception", e.message);
            }

            // Hide second (right-hand side) view, if there is no item to be displayed there
            if (i == 1 && item == null) {
                view?.visibility = View.INVISIBLE
            }
        }
    }

    private fun configureProtipView(holder: TwoItemsWithProtipRowViewHolder, position: Int) {

        // Get random protip from the original (fetched) list
        // Move this protip into 'used' list
        // If all protips from the original list have been used, then use random one from the 'used' list

        val protip: ProtipEntity?
        val protipText: String?

        if (!protipsList.isEmpty()) {
            protip =  protipsList.random()
            protipText = protip.protipText
            (protipsList as ArrayList).remove(protip)
            (usedProtipsList  as ArrayList).add(protip)
        } else {
            protip =  usedProtipsList.random()
            protipText = protip.protipText
        }

        holder.views[2].protip_text.text = protipText
    }

    abstract class ViewHolder (view: View) : RecyclerView.ViewHolder(view)

    open inner class TwoItemsRowViewHolder (view: View) : ViewHolder(view) {
        val views = ArrayList<View>()

        init {
            views.add(view.left_item)
            views.add(view.right_item)
        }
    }

    inner class TwoItemsWithProtipRowViewHolder (view: View) : TwoItemsRowViewHolder(view) {

        init {
            views.add(view.protip_item)
        }
    }

    // Converter grouping items together into 2-items clusters
    private fun convertSingleItemsListIntoClustersList(itemsList: List<ItemEntity>)
            : List<List<ItemEntity>> {
        val clustersList = ArrayList<List<ItemEntity>>()
        itemsList.chunked(2).forEach {
            clustersList.add(it)
        }
        return clustersList
    }
}