package dreamcatcher.howaboutit.features.appInfoView

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.features.basic.BasicFragment
import kotlinx.android.synthetic.main.app_info_view.*

// A view displaying contact and general app information
class AppInfoViewFragment : BasicFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.app_info_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup Cross Button
        val closingOnClickListener = View.OnClickListener{ activity?.onBackPressed() }
        btn_cross.setOnClickListener(closingOnClickListener)

        // Setup closing on the grey fields' click
        spacing_top.setOnClickListener(closingOnClickListener)
        spacing_bottom.setOnClickListener(closingOnClickListener)

        // Setup rating bar (stars) click listener
        ratingBar.setOnRatingBarChangeListener { _, _, _ ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_address)))
            startActivity(browserIntent)

            // Analytics event logging
            val context = context
            if (context != null) {
                FirebaseAnalytics.getInstance(context).logEvent(getString(R.string.analytics_event_feedback_stars_clicked), null)
            }
        }
    }
}