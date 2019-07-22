package dreamcatcher.howaboutit.features.feed

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.features.appInfoView.AppInfoViewFragment
import dreamcatcher.howaboutit.features.detailedView.DetailedViewFragment
import kotlinx.android.synthetic.main.activity_main_collapsing_toolbar.*
import kotlinx.android.synthetic.main.activity_main_top_panel.*
import kotlinx.android.synthetic.main.loading_badge.*
import java.util.*


// Main items feed) view
class FeedActivity : AppCompatActivity() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var generalListAdapter: GeneralListAdapter

    private val allItemsList = LinkedList<ItemEntity>()
    private val itemsToDisplay = LinkedList<ItemEntity>()

    private var connectionEstablishedFlag = false
    private var dataLoadingFinishedFlag = false

    private var toastMessage: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_collapsing_toolbar)

        // Setup progress bar animation
        animateProgressBar()

        // Initialize ViewModel
        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)

        // Initialize RecyclerView (items/ products list)
        setupRecyclerView()

        // Check if the application has connected to the server
        subscribeForConnectionStatus()

        // Catch and handle potential network issues
        subscribeForNetworkError()

        // Fetch items (products) from the file and load them into the view
        subscribeForItems()

        // Initialize search engine
        initializeSearchEngine()

        // Initialize app info button
        initializeAppInfoButton()
    }

    private fun filterResultsToDisplay(phrase: String) {

        itemsToDisplay.clear()

        // Check if the name of each element contains searched phrase. If not - remove this element.
        allItemsList.forEach {
            if (nameContainsSearchedPhrase(it, phrase) || tagsContainSearchedPhrase(it, phrase)) {
                itemsToDisplay.add(it)
            }
        }

        // Send a new list to adapter to display them.
        generalListAdapter.setItems(itemsToDisplay)
    }

    private fun nameContainsSearchedPhrase(itemEntity: ItemEntity, phrase: String): Boolean {
        return (itemEntity.name.toLowerCase().contains(phrase.toLowerCase()))
    }

    private fun tagsContainSearchedPhrase(itemEntity: ItemEntity, phrase: String): Boolean {
        itemEntity.tags.forEach {
            if (it.toLowerCase().contains(phrase.toLowerCase())) {
                return true
            }
        }
        return false
    }

    private fun initializeSearchEngine() {
        search_engine.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterResultsToDisplay(p0.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        general_recyclerview.layoutManager = layoutManager
        generalListAdapter = GeneralListAdapter{ itemId: String -> displayDetailedView(itemId) }
        general_recyclerview.adapter = generalListAdapter
    }

    private fun displayDetailedView(itemId: String) {

        val fragment = DetailedViewFragment()
        val bundle = Bundle()
        bundle.putString("itemId", itemId)
        fragment.arguments = bundle

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_content_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun subscribeForItems() {

        viewModel.getAllItems()?.observe(this, Observer<List<ItemEntity>> {
            if (!it.isNullOrEmpty()) {

                // Display fetched items
                allItemsList.clear()
                allItemsList.addAll(it)
                generalListAdapter.setItems(allItemsList)

                // Hide the loading view
                dataLoadingFinishedFlag = true
                if (connectionEstablishedFlag) {
                    showLoadingView(false)
                }
            }
        })
    }

    private fun subscribeForConnectionStatus() {

        // The delay is implemented to keep the loading screen visible to the user (for few seconds).
        Handler().postDelayed({

            viewModel.getConnectionEstablishedStatus()?.observe(this, Observer<Boolean> {

                // Hide the loading view
                connectionEstablishedFlag = true
                if (dataLoadingFinishedFlag) {
                    showLoadingView(false)
                }
            })
        }, 1000)
    }

    private fun subscribeForNetworkError() {
        viewModel.getNetworkError()?.observe(this, Observer<Boolean> {
            Handler().postDelayed({
                displayNetworkProblemMessage()
                retryConnection()
            }, 4000)
        })
    }

    private fun displayNetworkProblemMessage() {
        val isMessageCurrentlyDisplaying = toastMessage?.view?.isShown

        if (isMessageCurrentlyDisplaying != true) {
            if (toastMessage != null) {
                toastMessage?.cancel()
            }
            toastMessage = Toast.makeText(this, R.string.please_check_your_internet_connection, Toast.LENGTH_LONG)
            toastMessage?.show()
        }
    }

    private fun retryConnection() {
        viewModel.getAllItems()?.removeObservers(this)
        subscribeForItems()
    }

    private fun showLoadingView(loadingState: Boolean) {
        if (loadingState) {
            loading_container.visibility = View.VISIBLE
        } else {

            val fadeOutAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out_animation)
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(arg0: Animation) {}
                override fun onAnimationRepeat(arg0: Animation) {}
                override fun onAnimationEnd(arg0: Animation) {
                    loading_container.visibility = View.GONE
                }
            })

            loading_container.startAnimation(fadeOutAnimation)

            //loading_container.animate().alpha(0f).setDuration(2000).start()
            //loading_container.visibility = View.GONE
        }
    }

    private fun initializeAppInfoButton() {
        app_info_button.setOnClickListener {
            val fragment = AppInfoViewFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.main_content_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun animateProgressBar() {
        val rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.duration = 800
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatCount = Animation.INFINITE
        progressBar.startAnimation(rotateAnimation)
    }
}