package dreamcatcher.howaboutit.features.feed

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import de.hdodenhof.circleimageview.CircleImageView
import dreamcatcher.howaboutit.R
import dreamcatcher.howaboutit.data.database.items.ItemEntity
import dreamcatcher.howaboutit.data.database.protips.ProtipEntity
import dreamcatcher.howaboutit.databinding.ActivityMainCollapsingToolbarBinding
import dreamcatcher.howaboutit.features.appInfoView.AppAboutToBeDeprecatedView
import dreamcatcher.howaboutit.features.appInfoView.AppInfoViewFragment
import dreamcatcher.howaboutit.features.detailedView.DetailedViewFragment
import java.util.*
import kotlin.collections.ArrayList

// Main items feed) view
class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainCollapsingToolbarBinding
    private lateinit var loadingContainer: ConstraintLayout
    private lateinit var appInfoButton: CircleImageView
    private lateinit var generalRecyclerView: RecyclerView
    private lateinit var showMoreButton: Button
    private lateinit var noResultsView: LinearLayout
    private lateinit var yesPleaseButton: Button
    private lateinit var thankYouView: LinearLayout

    private lateinit var viewModel: FeedViewModel
    private lateinit var generalListAdapter: GeneralListAdapter

    private val allItemsList = ArrayList<ItemEntity>()
    private val itemsToDisplay = ArrayList<ItemEntity>()

    private val allProtipsList = ArrayList<ProtipEntity>()

    private var itemsFetchedSuccessfullyFlag = false
    private var protipsFetchedSuccessfullyFlag = false

    private var toastMessage: Toast? = null

    private val handler = Handler()

    private var allowedItemsAmount = 24

    private var mostRecentSearchPhrase = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainCollapsingToolbarBinding.inflate(layoutInflater)
        loadingContainer = binding.loadingContainer.loadingContainer
        appInfoButton = binding.mainTopPanel.appInfoButton
        generalRecyclerView = binding.generalRecyclerview
        showMoreButton = binding.showMoreButton
        noResultsView = binding.noResultsView
        yesPleaseButton = binding.noResultsViewContainer.yesPleaseButton
        thankYouView = binding.thankYouView

        val view = binding.root
        setContentView(view)

        // Setup progress bar animation
        animateProgressBar()

        // Initialize ViewModel
        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)

        // Initialize RecyclerView (items/ products list)
        setupRecyclerView()

        // Catch and handle potential network issues
        subscribeForNetworkError()

        // Fetch items (products) from the backend and load them into the view
        updateAndLoadItems()

        // Fetch protips from the backend and load them into the view
        updateAndLoadProtips()

        // Initialize search engine
        initializeSearchEngine()

        // Initialize app info button
        initializeAppInfoButton()

        // Initialize "Show more" button
        initializeShowMoreButton()

        // Initialize "Yes please" button
        initializeYesPleaseButton()

        // Initialize notifications service
        initializeNotificationsService()

        // Check the user's language (to inform that the app's content is only available in Polish language)
        languageCheck()

        // Lock the free app version
//        lockTheApp()
    }

    override fun onResume() {
        super.onResume()

        // Prevent displaying the keyboard when the app is resumed from the background
        currentFocus?.clearFocus()
    }

    override fun onBackPressed() {
        if (mostRecentSearchPhrase != "") resetSearchResults()
        else super.onBackPressed()
    }

    private fun resetSearchResults() {
        binding.mainTopPanel.searchEngine.text.clear()
        allowedItemsAmount = 24
        searchAction()
    }

    private fun filterResultsToDisplay(phrase: String) {

        itemsToDisplay.clear()

        // Case when we have searched for sth and we remove input (we want to see all items again).
        if (phrase == "") {
            allItemsList.forEach {
                itemsToDisplay.add(it)
            }
        } else {
            val matchesRanking = ArrayList<Pair<Int, ItemEntity>>()
            allItemsList.forEach {
                //itemsToDisplay.add(it)
                val matchedTags = tagsContainingSearchedPhrase(it, phrase)
                if (matchedTags > 0) {
                    matchesRanking.add(Pair(matchedTags, it))
                }
            }
            // Sort the elements to display those items that math most tags.
            matchesRanking.sortByDescending { it.first }
            var mostMatchedTags = 0
            if (matchesRanking.isNotEmpty()) {
                mostMatchedTags = matchesRanking.get(0).first
                matchesRanking.forEach {
                    if (it.first == mostMatchedTags) {
                        itemsToDisplay.add(it.second)
                    }
                }
            }
        }

        allowedItemsAmount = 24

        // Send a new list to adapter to display them.
        updateDisplayedItems()

        // Update ShowMore button visibility.
        updateShowMoreButton()

        // Analytics event logging.
        /*if (itemsToDisplay.isEmpty()) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, phrase)
            FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        }*/
    }

    private fun tagsContainingSearchedPhrase(itemEntity: ItemEntity, phrase: String): Int {
        val editedPhrase = normalizePolishCharacters(phrase.toLowerCase())
        var tagsMatched = 0
        itemEntity.tags.forEach {
            val editedTag = normalizePolishCharacters(it.toLowerCase())
            if (editedPhrase.contains(editedTag) || editedTag.contains(editedPhrase)) {
                tagsMatched++
            }
        }
        return tagsMatched
    }

    private fun normalizePolishCharacters(input: String): String {
        var output = ""
        input.map {
            when (it) {
                'ą' -> "a"
                'ć' -> "c"
                'ę' -> "e"
                'ł' -> "l"
                'ń' -> "n"
                'ó' -> "o"
                'ś' -> "s"
                'ż' -> "z"
                'ź' -> "z"
                else -> it
            }.let {
                output += it
            }
        }
        return output
    }

    private fun initializeSearchEngine() {

        binding.mainTopPanel.searchEngine.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {

                if (p0.toString().equals("")) {
                    handler.post {
                        resetSearchResults()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        // 'Enter' key on the keyboard
        binding.mainTopPanel.searchEngine.setOnEditorActionListener { v, actionId, event ->
            if (actionId != EditorInfo.IME_ACTION_NONE && actionId != EditorInfo.IME_ACTION_PREVIOUS) {
                searchAction()
                true
            } else {
                false
            }
        }

        // 'Search' button
        binding.mainTopPanel.searchButton.setOnClickListener{
            searchAction()
        }
    }

    private fun searchAction() {

        allowedItemsAmount = 24

        binding.mainTopPanel.searchButton.isEnabled = false

        hideNoResultsAndThankYouViews()

        if (!isItTheSameSearchAsCurrentlyDisplayed()) {
            mostRecentSearchPhrase = binding.mainTopPanel.searchEngine.text.toString()
            filterResultsToDisplay(mostRecentSearchPhrase)

            // Button color change.
            binding.mainTopPanel.searchButton.postDelayed({
                binding.mainTopPanel.searchButton.isEnabled = true
            }, 500)
        } else {

            // Button color change.
            binding.mainTopPanel.searchButton.postDelayed({
                binding.mainTopPanel.searchButton.isEnabled = true
            }, 100)
        }

        hideKeyboard()
    }

    private fun isItTheSameSearchAsCurrentlyDisplayed(): Boolean {
        return (binding.mainTopPanel.searchEngine.text.toString().equals(mostRecentSearchPhrase))
    }

    private fun setupRecyclerView() {
        generalRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        generalRecyclerView.layoutManager = layoutManager
        generalListAdapter = GeneralListAdapter{ itemId: String -> displayDetailedView(itemId) }
        generalListAdapter.setHasStableIds(true)
        generalRecyclerView.adapter = generalListAdapter
    }

    private fun displayDetailedView(itemId: String) {

        // Hide the keyboard just in case to avoid problems with the fragment view displaying
        // It would be better to have this in Fragment - because of encapsulation - but then it's not so fluent.
        hideKeyboard()

        val fragment = DetailedViewFragment()
        val bundle = Bundle()
        bundle.putString("itemId", itemId)
        fragment.arguments = bundle

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_content_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateAndLoadItems() {
        viewModel.updateItemsDatabaseWithServer()?.observe(this, Observer<Boolean> {
            if (it == true) loadItemsFromDatabase()
        })
    }

    // Before there was a delay implemented to keep the loading screen visible to the user (for few seconds).
    private fun updateAndLoadProtips() {
        viewModel.updateProtipsDatabaseWithServer()?.observe(this, Observer<Boolean> {
            if (it == true) loadProtipsFromDatabase()
        })
    }

    private fun loadItemsFromDatabase() {
        viewModel.getAllItems()?.observe(this, Observer<List<ItemEntity>> {

            if (!it.isNullOrEmpty()) {

                // For some weird reason the data comes first in smaller parties. I don't know why,
                // but to fix it I have added this 'if' condition temporarily.
                // Probably that's the reason of "repeated items issue".
                if (it.size > allowedItemsAmount) {

                    // Display fetched items (using adapter)
                    allItemsList.clear()
                    // Mix the items to avoid the same order on every app launch.
                    allItemsList.addAll(it.shuffled())
                    itemsFetchedSuccessfullyFlag = true
                    sendItemsAndProtipsToAdapter()
                }
            }
        })
    }

    private fun loadProtipsFromDatabase() {
        viewModel.getAllProtips()?.observe(this, Observer<List<ProtipEntity>> {

            if (!it.isNullOrEmpty()) {

                // Display fetched items (using adapter)
                allProtipsList.clear()
                allProtipsList.addAll(it)
                protipsFetchedSuccessfullyFlag = true
                sendItemsAndProtipsToAdapter()
            }
        })
    }

    private fun subscribeForNetworkError() {
        viewModel.getNetworkError()?.observe(this, Observer<String> {

            Handler().postDelayed({
                displayNetworkProblemMessage()
                retryConnection()
            }, 600)

            // Analytics event logging.
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.VALUE, it)
            // Temporary event name.
//            FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle)
        })
    }

    private fun sendItemsAndProtipsToAdapter() {
        if (itemsFetchedSuccessfullyFlag && protipsFetchedSuccessfullyFlag) {

            // Sometimes index is out of bound. It's important to ivestigate this.
            try {
                itemsToDisplay.addAll(allItemsList)
                generalListAdapter.setItemsAndProtips(itemsToDisplay.subList(0, allowedItemsAmount), allProtipsList)

                // Hide the loading view
                showLoadingView(false)
            } catch (e: Exception) {
                //Log.e("Exception", e.message);
            }
        }
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
        viewModel.updateItemsDatabaseWithServer()?.removeObservers(this)
        viewModel.updateProtipsDatabaseWithServer()?.removeObservers(this)
        updateAndLoadItems()
        updateAndLoadProtips()
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(view.windowToken, 0) }
        }
    }

    private fun showLoadingView(loadingState: Boolean) {

        if (loadingState) {
            loadingContainer.visibility = View.VISIBLE
        } else {
            val fadeOutAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out_animation)
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(arg0: Animation) {}
                override fun onAnimationRepeat(arg0: Animation) {}
                override fun onAnimationEnd(arg0: Animation) {
                    loadingContainer.visibility = View.GONE
                }
            })

            // We add some delay to give images more time to be loaded properly.
            /*loadingContainer.postDelayed({
                loadingContainer.startAnimation(fadeOutAnimation)
            }, 1000)*/

            loadingContainer.doOnLayout {

                // These settings allow user to interact with content before the animation is finished.
                loadingContainer.isClickable = false
                loadingContainer.isFocusable = false

                loadingContainer.startAnimation(fadeOutAnimation)
            }
        }
    }

    private fun languageCheck() {
        val alertDialog = AlertDialog.Builder(this@FeedActivity).create()
        alertDialog.setTitle(getString(dreamcatcher.howaboutit.R.string.language_issue))
        alertDialog.setMessage(getString(dreamcatcher.howaboutit.R.string.we_are_sorry_but_currently))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(dreamcatcher.howaboutit.R.string.ok))
        {
                dialog, which ->
            dialog.dismiss()
        }

        if (Locale.getDefault().getDisplayLanguage() != "polski") {
            alertDialog.show()
        }
    }

    private fun lockTheApp() {
        val fragment = AppAboutToBeDeprecatedView()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_content_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun initializeAppInfoButton() {

        val rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.duration = 2500
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.repeatCount = Animation.INFINITE
        appInfoButton.startAnimation(rotateAnimation)

        appInfoButton.setOnClickListener {

            // Hide the keyboard just in case to avoid problems with the fragment view displaying
            // It would be better to have this in Fragment - because of encapsulation - but then it's not so fluent.
            hideKeyboard()

            val fragment = AppInfoViewFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.main_content_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initializeShowMoreButton() {

        showMoreButton.setOnClickListener {

            allowedItemsAmount += 24

            // Send a new list to adapter to display them.
            updateDisplayedItems()

            // Update ShowMore button visibility.
            updateShowMoreButton()
        }
    }

    private fun initializeYesPleaseButton() {

        yesPleaseButton.setOnClickListener {

            // Update views' visibility.
            noResultsView.visibility = View.GONE
            thankYouView.visibility = View.VISIBLE

            // Analytics event logging.
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, binding.mainTopPanel.searchEngine.text.toString())
            FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        }
    }

    private fun initializeNotificationsService() {

        // Get token (we need it for notifications testing)
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    //Log.w(TAG, "getInstanceId failed", task.exception)
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
//                token?.let {
//                    Log.d("Token:", token)
//                }
//            })

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create channel to show notifications.
//            val channelId = getString(R.string.default_notification_channel_id)
//            val channelName = getString(R.string.default_notification_channel_name)
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager?.createNotificationChannel(
//                NotificationChannel(channelId,
//                    channelName, NotificationManager.IMPORTANCE_LOW)
//            )
//        }

    }

    private fun updateDisplayedItems() {

        // Sometimes index is out of bound. It's important to investigate this.
        try {

            if (itemsToDisplay.size <= allowedItemsAmount) {
                generalListAdapter.updateItems(itemsToDisplay)
            } else {
                generalListAdapter.updateItems(itemsToDisplay.subList(0, allowedItemsAmount))
            }

            // Check "No results" view displaying.
            if (itemsToDisplay.size == 0) noResultsView.visibility = View.VISIBLE

            // Hide the loading view
            showLoadingView(false)
        } catch (e: Exception) {
            //Log.e("Exception", e.message);
        }
    }

    private fun updateShowMoreButton() {
        if (itemsToDisplay.size <= allowedItemsAmount) showMoreButton.visibility = View.GONE
        else showMoreButton.visibility = View.VISIBLE
    }

    private fun hideNoResultsAndThankYouViews () {
        noResultsView.visibility = View.GONE
        thankYouView.visibility = View.GONE
    }

    private fun animateProgressBar() {
        val rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.duration = 800
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatCount = Animation.INFINITE
        binding.loadingContainer.progressBar.startAnimation(rotateAnimation)
    }
}