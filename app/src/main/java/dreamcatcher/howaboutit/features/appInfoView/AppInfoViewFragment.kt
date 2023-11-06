package dreamcatcher.howaboutit.features.appInfoView

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.databinding.AppAboutToBeDeprecatedViewBinding
import dreamcatcher.howaboutit.databinding.AppInfoViewBinding
import dreamcatcher.howaboutit.features.basic.BasicFragment

// A view displaying contact and general app information
class AppInfoViewFragment : BasicFragment() {

    private lateinit var binding: AppInfoViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AppInfoViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup Cross Button
        val closingOnClickListener = View.OnClickListener{ activity?.onBackPressed() }
        binding.btnCross.setOnClickListener(closingOnClickListener)

        // Setup closing on the grey fields' click
        binding.spacingTop.setOnClickListener(closingOnClickListener)
        binding.spacingBottom.setOnClickListener(closingOnClickListener)

        // Setup rating bar (stars) click listener
        binding.ratingBar.setOnRatingBarChangeListener { _, _, _ ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_address)))
            startActivity(browserIntent)

            // Analytics event logging
            val context = context
            if (context != null) {
                FirebaseAnalytics.getInstance(context).logEvent(getString(R.string.analytics_event_feedback_stars_clicked), null)
            }
        }

        // Setup Facebook button
        binding.btnFacebook.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_address)))
            startActivity(browserIntent)

            // Analytics event logging
            val context = context
            if (context != null) {
                //FirebaseAnalytics.getInstance(context).logEvent(getString(R.string.analytics_event_feedback_stars_clicked), null)
            }
        }

        // Setup privacy policy click listener
        binding.privacyPolicyLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_address)))
            startActivity(browserIntent)
        }
    }
}