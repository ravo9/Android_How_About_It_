package dreamcatcher.howaboutit.features.appInfoView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dreamcatcher.howaboutit.R
import kotlinx.android.synthetic.main.detailed_item_view.*

// A view displaying contact and general app information
class AppInfoViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.app_info_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup Cross Button
        btn_cross.setOnClickListener{
           activity?.onBackPressed()
        }
    }
}