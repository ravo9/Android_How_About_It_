package dreamcatcher.howaboutit.features.feed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import dreamcatcher.howaboutit.R


// Main items feed) view
class FeedActivity : AppCompatActivity() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var itemsGridAdapter: ItemsGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)

        // Initialize RecyclerView (items/ products list)
        //setupRecyclerView()

        // Fetch items (products) from the file and load them into the view
        //subscribeForItems()

        // Catch and handle potential network issues
        //subscribeForNetworkError()
    }

    /*private fun setupSideDrawer() {

        // Initialize Side Drawer layout
        val drawerListener = ActionBarDrawerToggle(this, main_layout_container, R.string.sort, R.string.filter)
        main_layout_container.addDrawerListener(drawerListener)
        drawerListener.syncState()

        // Set Filter button onClick listener
        textView_filter.setOnClickListener{
            main_layout_container.openDrawer(GravityCompat.END)
        }

        // Set (Side Drawer's) Close button onClick listener
        close_btn.setOnClickListener{
            main_layout_container.closeDrawer(GravityCompat.END)
        }
    }*/

    /*private fun setupRecyclerView() {
        itemsGridAdapter = ItemsGridAdapter(this)
        gridView_products.adapter = itemsGridAdapter
    }*/

    /*private fun subscribeForItems() {
        viewModel.getAllItems()?.observe(this, Observer<List<ItemEntity>> {

            // Display fetched items
            if (!it.isNullOrEmpty()) {
                itemsGridAdapter.setItems(it)
            }
        })
    }*/

    /*private fun subscribeForNetworkError() {
        viewModel.getNetworkError()?.observe(this, Observer<Boolean> {

            // Display the "Connection problem" dialogbox
            displayConnectionProblemDialogbox()
        })
    }*/

    /*private fun refreshItemsSubscription() {

        // Reset items subscription (if no items have been cached as far)
        if (itemsGridAdapter.count == 0) {
            viewModel.getAllItems()?.removeObservers(this)
            subscribeForItems()
        }
    }*/

    /*private fun displayConnectionProblemDialogbox() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(R.string.there_is_a_problem_connecting)
            .setTitle(R.string.connection_problem)

        builder.setPositiveButton(R.string.ok) { _, _ ->
            refreshItemsSubscription()
        }

        val dialog = builder.create()
        dialog.show()
    }*/
}