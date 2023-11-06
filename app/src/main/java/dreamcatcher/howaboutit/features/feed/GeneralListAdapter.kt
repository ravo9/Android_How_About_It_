package dreamcatcher.howaboutit.features.feed

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dreamcatcher.howaboutit.data.database.items.ItemEntity
import dreamcatcher.howaboutit.data.database.protips.ProtipEntity
import dreamcatcher.howaboutit.databinding.GridSingleItemBinding
import dreamcatcher.howaboutit.databinding.TwoItemsRowBinding
import dreamcatcher.howaboutit.databinding.TwoItemsWithProtipRowBinding

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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        // Return 0 or 1 per each position to indicate viewtype.
        return position % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {

            VIEW_TYPE_TWO_ITEMS_ROW -> {
                val binding = TwoItemsRowBinding.inflate(inflater, parent, false)
                TwoItemsRowViewHolder(binding)
            }

            VIEW_TYPE_TWO_ITEMS_WITH_PROTIP_ROW -> {
                val binding = TwoItemsWithProtipRowBinding.inflate(inflater, parent, false)
                TwoItemsWithProtipRowViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
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

    private fun configureLeftAndRightItemsViews(holder: ViewHolder, position: Int) {

        var view: GridSingleItemBinding?
        var item: ItemEntity?

        for (i in 0..1) {

            // Clear variables
            view = null
            item = null

            try {

                // Select view
                if (holder is TwoItemsRowViewHolder) {
                    view = if (i == 0) holder.leftItem
                    else holder.rightItem
                }
                if (holder is TwoItemsWithProtipRowViewHolder) {
                    view = if (i == 0) holder.leftItem
                    else holder.rightItem
                }

                // Ensure the view is visible (it could be hidden before)
                view?.root?.visibility = View.VISIBLE

                // Select an item
                item = itemsList[position][i]

                // Prepare fetched data
                val name = item.name
                val imageLink = item.imageLink

                // Set data within the holder
                view?.name?.text = name

                // Set onClickListener
                val itemId = item.id
                view?.root?.setOnClickListener{
                    clickListener(itemId)
                }

                // Load thumbnail
                if (!imageLink.isNullOrEmpty()) {
                    Picasso.get().load(imageLink).into(view?.thumbnail)
                    view?.thumbnailPlaceholderText?.visibility = View.INVISIBLE
                } else {
                    view?.thumbnail?.setImageDrawable(null)
                    view?.thumbnailPlaceholderText?.visibility = View.VISIBLE
                }

            } catch(e: Exception) {
                val exceptionMessage = e.message ?: "No exception message"
                Log.e("Exception", exceptionMessage)
            }

            // Hide second (right-hand side) view, if there is no item to be displayed there
            if (i == 1 && item == null) {
                view?.root?.visibility = View.INVISIBLE
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

        holder.protipItem.protipText.text = protipText
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class TwoItemsRowViewHolder(binding: TwoItemsRowBinding) : ViewHolder(binding.root) {
        val leftItem = binding.leftItem
        val rightItem = binding.rightItem
    }

    inner class TwoItemsWithProtipRowViewHolder(binding: TwoItemsWithProtipRowBinding) : ViewHolder(binding.root) {
        val leftItem = binding.leftItem
        val rightItem = binding.rightItem
        val protipItem = binding.protipItem
    }

    // Converter grouping items together into 2-items clusters
    private fun convertSingleItemsListIntoClustersList(itemsList: List<ItemEntity>) : List<List<ItemEntity>> {
        val clustersList = ArrayList<List<ItemEntity>>()
        itemsList.chunked(2).forEach { clustersList.add(it) }
        return clustersList
    }
}