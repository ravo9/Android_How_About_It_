package dreamcatcher.howaboutit.features.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.features.appInfoView.AppInfoViewFragment
import dreamcatcher.howaboutit.features.detailedView.DetailedViewFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_top_panel.*
import kotlinx.android.synthetic.main.loading_badge.*
import java.util.*

// Main items feed) view
class FeedActivity : AppCompatActivity() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var itemsGridAdapter: ItemsGridAdapter

    private val allItemsList = LinkedList<ItemEntity>()
    private val itemsToDisplay = LinkedList<ItemEntity>()

    private var connectionEstablishedFlag = false
    private var dataLoadingFinishedFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_collapsing_toolbar)

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

        // Setup (hidden) "Retry connection" button
        tryagain_button.setOnClickListener {
            retryConnection()
        }
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
        itemsGridAdapter.setItems(itemsToDisplay)
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
        itemsGridAdapter = ItemsGridAdapter(this){ itemId: String -> displayDetailedView(itemId) }
        gridView.adapter = itemsGridAdapter
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
                itemsGridAdapter.setItems(allItemsList)

                // Hide the loading view
                dataLoadingFinishedFlag = true
                if (connectionEstablishedFlag) {
                    showLoadingView(false)
                }
            }
        })
    }

    private fun subscribeForConnectionStatus() {
        viewModel.getConnectionEstablishedStatus()?.observe(this, Observer<Boolean> {

            // Hide the loading view
            connectionEstablishedFlag = true
            if (dataLoadingFinishedFlag) {
                showLoadingView(false)
            }
        })
    }

    private fun subscribeForNetworkError() {
        viewModel.getNetworkError()?.observe(this, Observer<Boolean> {

            // Display the "Retry connection" button
            tryagain_button.visibility = View.VISIBLE
        })
    }

    private fun retryConnection() {
        viewModel.getAllItems()?.removeObservers(this)
        subscribeForItems()
    }

    private fun showLoadingView(loadingState: Boolean) {
        if (loadingState) {
            loading_container.visibility = View.VISIBLE
            top_interface_bar_container.visibility = View.GONE
        } else {
            loading_container.visibility = View.GONE
            top_interface_bar_container.visibility = View.VISIBLE
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
}