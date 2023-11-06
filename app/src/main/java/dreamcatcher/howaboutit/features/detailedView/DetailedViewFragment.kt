package dreamcatcher.howaboutit.features.detailedView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import dreamcatcher.howaboutit.data.database.items.ItemEntity
import dreamcatcher.howaboutit.databinding.DetailedItemViewBinding
import dreamcatcher.howaboutit.features.basic.BasicFragment

// Detailed view for displaying chosen item
class DetailedViewFragment : BasicFragment() {

    private lateinit var viewModel: DetailedViewViewModel

    private lateinit var binding: DetailedItemViewBinding
    private lateinit var btnCross: ImageButton
    private lateinit var spacingTop: RelativeLayout
    private lateinit var spacingBottom: RelativeLayout
    private lateinit var detailedItemViewName: TextView
    private lateinit var detailedItemViewRecyclingSteps: TextView
    private lateinit var detailedItemViewAdditionalInfo: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var picturePlaceholderText: TextView
    private lateinit var thumbnail: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Initialize ViewModel
        viewModel = ViewModelProviders.of(this).get(DetailedViewViewModel::class.java)

        binding = DetailedItemViewBinding.inflate(inflater, container, false)
        btnCross = binding.btnCross
        spacingTop = binding.spacingTop
        spacingBottom = binding.spacingBottom
        detailedItemViewName = binding.detailedItemViewName
        detailedItemViewRecyclingSteps = binding.detailedItemViewRecyclingSteps
        detailedItemViewAdditionalInfo = binding.detailedItemViewAdditionalInfo
        progressBar = binding.progressBar
        picturePlaceholderText = binding.picturePlaceholderText
        thumbnail = binding.thumbnail
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Fetch detailed data from Data Repository
        subscribeForItem()

        // Setup Cross Button
        val closingOnClickListener = View.OnClickListener{ activity?.onBackPressed() }
        btnCross.setOnClickListener(closingOnClickListener)

        // Setup closing on the grey fields' click
        spacingTop.setOnClickListener(closingOnClickListener)
        spacingBottom.setOnClickListener(closingOnClickListener)
    }

    private fun subscribeForItem() {
        val itemId = this.arguments?.getString("itemId")
        if (!itemId.isNullOrEmpty()) {
            viewModel.getSingleSavedItemById(itemId)?.observe(viewLifecycleOwner) {
                setupDetailedView(it)
            }
        }
    }

    // Prepare view to display fetched item
    private fun setupDetailedView(item: ItemEntity) {

        // Set item's name
        detailedItemViewName.text = item.name

        // Set bin type's text
        detailedItemViewRecyclingSteps.text = item.binType

        // Set "additional information" to display
        if (item.additionalInformation != null) {
            var additionalInformationText = ""

            for (phrase in item.additionalInformation) {
                additionalInformationText += "✔"
                additionalInformationText += "  "
                additionalInformationText += phrase
                additionalInformationText += "\n\n"
            }
            detailedItemViewAdditionalInfo.text = additionalInformationText
        }

        // Load thumbnail
        val imageUrl = item.imageLink
        try { Glide.with(requireContext()).load(imageUrl).into(thumbnail); }
        catch (e: Exception) {
            picturePlaceholderText.visibility = View.VISIBLE
            val exceptionMessage = e.message ?: "No exception message"
            Log.e("Exception", exceptionMessage)
        }

        showLoadingView(false)
    }

    private fun showLoadingView(loadingState: Boolean) = if (loadingState) {
        progressBar.visibility = View.VISIBLE
    } else {
        progressBar.visibility = View.GONE
    }
}