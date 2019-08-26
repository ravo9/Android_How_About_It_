package dreamcatcher.howaboutit.features.detailedView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.items.ItemEntity
import dreamcatcher.howaboutit.features.basic.BasicFragment
import kotlinx.android.synthetic.main.detailed_item_view.*

// Detailed view for displaying chosen item
class DetailedViewFragment : BasicFragment() {

    private lateinit var viewModel: DetailedViewViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Initialize ViewModel
        viewModel = ViewModelProviders.of(this).get(DetailedViewViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.detailed_item_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Fetch detailed data from Data Repository
        subscribeForItem()

        // Setup Cross Button
        val closingOnClickListener = View.OnClickListener{ activity?.onBackPressed() }
        btn_cross.setOnClickListener(closingOnClickListener)

        // Setup closing on the grey fields' click
        spacing_top.setOnClickListener(closingOnClickListener)
        spacing_bottom.setOnClickListener(closingOnClickListener)
    }

    private fun subscribeForItem() {
        val itemId = this.arguments?.getString("itemId")
        if (!itemId.isNullOrEmpty()) {
            viewModel.getSingleSavedItemById(itemId)?.observe(this, Observer<ItemEntity> {
                setupDetailedView(it)
            })
        }
    }

    // Prepare view to display fetched item
    private fun setupDetailedView(item: ItemEntity) {

        // Set item's name
        detailed_item_view_name.text = item.name

        // Set bin type's text
        detailed_item_view_recycling_steps.text = item.binType

        // Set "additional information" to display
        if (item.additionalInformation != null) {
            var additionalInformationText = ""

            for (phrase in item.additionalInformation) {
                additionalInformationText += "✔"
                additionalInformationText += "  "
                additionalInformationText += phrase
                additionalInformationText += "\n\n"
            }
            detailed_item_view_additional_info.text = additionalInformationText
        }

        // Load thumbnail
        val imageUrl = item.imageLink
        try { Glide.with(context!!).load(imageUrl).into(thumbnail); }
        catch (e: Exception) {
            picture_placeholder_text.visibility = View.VISIBLE
            Log.e("Exception", e.message);
        }

        showLoadingView(false)
    }

    private fun showLoadingView(loadingState: Boolean) {
        if (loadingState) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}