package dreamcatcher.howaboutit.features.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.ItemEntity
import dreamcatcher.howaboutit.features.detailedView.DetailedViewFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_top_panel.*
import java.util.*

// Main items feed) view
class FeedActivity : AppCompatActivity() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var itemsGridAdapter: ItemsGridAdapter
    private val allItemsList = LinkedList<ItemEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)

        // Initialize RecyclerView (items/ products list)
        setupRecyclerView()

        // Fetch items (products) from the file and load them into the view
        subscribeForItems()

        // Catch and handle potential network issues
        subscribeForNetworkError()

        // Initialize search engine
        search_engine.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterResultsToDisplay(p0.toString())
            }
        })
    }

    private fun filterResultsToDisplay(phrase: String) {

        val itemsToDisplay = LinkedList<ItemEntity>()

        // Check if the name of each element contains searched phrase. If not - remove this element.
        allItemsList.forEach {
            if (it.name.toLowerCase().contains(phrase.toLowerCase())) {
                itemsToDisplay.add(it)
            }
        }

        // Send a new list to adapter to display them.
        itemsGridAdapter.setItems(itemsToDisplay)
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

            // Display fetched items
            if (!it.isNullOrEmpty()) {
                allItemsList.addAll(it)
                itemsGridAdapter.setItems(allItemsList)
            }
        })
    }

    private fun subscribeForNetworkError() {
        viewModel.getNetworkError()?.observe(this, Observer<Boolean> {

            // Display the "Connection problem" dialogbox
            displayConnectionProblemDialogbox()
        })
    }

    private fun refreshItemsSubscription() {

        // Reset items subscription (if no items have been cached as far)
        if (itemsGridAdapter.count == 0) {
            viewModel.getAllItems()?.removeObservers(this)
            subscribeForItems()
        }
    }

    private fun displayConnectionProblemDialogbox() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(R.string.there_is_a_problem_connecting)
            .setTitle(R.string.connection_problem)

        builder.setPositiveButton(R.string.ok) { _, _ ->
            refreshItemsSubscription()
        }

        val dialog = builder.create()
        dialog.show()
    }
}