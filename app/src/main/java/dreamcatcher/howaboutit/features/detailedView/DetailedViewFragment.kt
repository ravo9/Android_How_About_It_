package dreamcatcher.howaboutit.features.detailedView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import dreamcatcher.howaboutit.R
import kotlinx.android.synthetic.main.detailed_item_view.*


// Detailed view for displaying chosen item
class DetailedViewFragment : Fragment() {

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
        //subscribeForArticle()

        // Setup Cross Button
        btn_cross.setOnClickListener{
           activity?.onBackPressed()
        }
    }

    /*private fun subscribeForArticle() {
        val articleId = this.arguments?.getString("articleId")
        if (!articleId.isNullOrEmpty()) {
            viewModel.getSingleSavedArticleById(articleId)?.observe(this, Observer<ArticleEntity> {
                setupWebView(it.contentUrl)
            })
        }
    }*/

    // Setup website view
    /*private fun setupWebView(url: String) {
        website_view.settings.javaScriptEnabled = true
        website_view.webViewClient = WebViewClient()
        website_view.loadUrl(url)
        showLoadingView(false)
    }

    private fun showLoadingView(loadingState: Boolean) {
        if (loadingState) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }*/
}