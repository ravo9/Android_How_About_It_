package dreamcatcher.howaboutit.features.appInfoView

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.databinding.AppAboutToBeDeprecatedViewBinding
import dreamcatcher.howaboutit.features.basic.BasicFragment

// A view displaying contact and general app information
class AppAboutToBeDeprecatedView : BasicFragment() {

    private lateinit var binding: AppAboutToBeDeprecatedViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AppAboutToBeDeprecatedViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup Cross and Close Buttons
        val closingOnClickListener = View.OnClickListener{ activity?.onBackPressed() }
        binding.btnCross.setOnClickListener(closingOnClickListener)
        binding.btnClose.setOnClickListener(closingOnClickListener)

        // Setup closing on the grey fields' click
        binding.spacingTop.setOnClickListener(closingOnClickListener)
        binding.spacingBottom.setOnClickListener(closingOnClickListener)

        // Setup download button
        binding.btnGetProVersion.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_address_pro_version)))
            startActivity(browserIntent)
        }
    }
}