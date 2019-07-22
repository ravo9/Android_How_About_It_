package dreamcatcher.howaboutit.features.feed

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.ItemEntity
import kotlinx.android.synthetic.main.grid_single_item.view.*
import java.util.*

// Main adapter used for managing items grid within the main Feed View
class ItemsGridAdapter (val clickListener: (String) -> Unit, list: List<ItemEntity>?, private val context: Context) : BaseAdapter() {

    private var itemsList = ArrayList<ItemEntity>()

    init {
        if (list != null && list.isNotEmpty()) {
            itemsList.addAll(list)
        }
    }

    override fun getCount(): Int {
        return itemsList.size
    }

    override fun getItem(position: Int): Any {
        return itemsList[position]
    }

    override fun getItemId(position: Int): Long {
        //return itemsList[position].id!!.toLong()
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // Inflate the view
        val item = this.itemsList[position]
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.grid_single_item, null)

        // Prepare data values
        val name = item.name

        // Set data within the view
        itemView.name.text = name

        // Load thumbnail
        val imageUrl = item.imageLink
        val thumbnail = itemView.thumbnail
        try { Glide.with(context).load(imageUrl).into(thumbnail); }
        catch (e: Exception) {
            Log.e("Exception", e.message);
        }

        // Set onClickListener
        itemView.setOnClickListener{
            val itemId = this.itemsList[position].id
            clickListener(itemId)
        }

        return itemView
    }
}