package dreamcatcher.howaboutit.features.appInfoView

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.features.basic.BasicFragment
import kotlinx.android.synthetic.main.app_about_to_be_deprecated_view.*
import kotlinx.android.synthetic.main.app_info_view.btn_cross
import kotlinx.android.synthetic.main.app_info_view.spacing_bottom
import kotlinx.android.synthetic.main.app_info_view.spacing_top

// A view displaying contact and general app information
class AppAboutToBeDeprecatedView : BasicFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.app_about_to_be_deprecated_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup Cross and Close Buttons
        val closingOnClickListener = View.OnClickListener{ activity?.onBackPressed() }
        btn_cross.setOnClickListener(closingOnClickListener)
        btn_close.setOnClickListener(closingOnClickListener)

        // Setup closing on the grey fields' click
        spacing_top.setOnClickListener(closingOnClickListener)
        spacing_bottom.setOnClickListener(closingOnClickListener)

        // Setup download button
        btn_get_pro_version.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_address_pro_version)))
            startActivity(browserIntent)
        }
    }
}