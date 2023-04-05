package com.example.submission.ui.main

import com.example.submission.R
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submission.databinding.ActivityMainBinding
import com.example.submission.shared.dataStore
import com.example.submission.ui.ViewModelFactory
import com.example.submission.ui.settings.SettingsActivity
import com.example.submission.ui.favorite.FavoriteActivity
import com.example.submission.ui.settings.SettingPreferences
import com.example.submission.ui.settings.SettingsViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var rvUsers: RecyclerView? = null
    private lateinit var searchView: SearchView
    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeDarkMode()
        setupUI()
        setupProgressBar()
        setupErrorPage()
        setupRecyclerView()
    }

    private fun observeDarkMode() {
        val pref = SettingPreferences.getInstance(dataStore)
        val settingsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref))[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.searchView_query_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.searchUsers(query)
                }

                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val searchViewMenuItem: MenuItem = menu.findItem(R.id.search)
        searchViewMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                if (viewModel.searchQuery.value?.isNotEmpty() == true) {
                    binding?.textViewSearchInfo?.visibility = View.VISIBLE
                }
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                binding?.textViewSearchInfo?.visibility = View.GONE
                return true
            }
        })

        observeData()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                val moveToFavoriteActivity = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(moveToFavoriteActivity)
            }
            R.id.settings -> {
                val moveToSettingsActivity = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(moveToSettingsActivity)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }

    private fun setupErrorPage() {
        viewModel.isError.observe(this) { isError ->
            with(binding) {
                this?.content?.isVisible = !isError
                this?.layoutNoResult?.textViewEmpty?.isVisible = !isError
                this?.layoutNoResult?.textViewError?.isVisible = isError
            }
        }
    }

    private fun observeData() {
        viewModel.users.observe(this) { users ->
            if (users.isEmpty()) {
                binding?.apply {
                    layoutNoResult.textViewEmpty.visibility = View.VISIBLE
                    content.isVisible = false
                }
            } else {
                if (searchView.query.isEmpty()) {
                    binding?.textViewSearchInfo?.visibility = View.GONE
                } else {
                    binding?.textViewSearchInfo?.visibility = View.VISIBLE
                }

                binding?.layoutNoResult?.textViewEmpty?.visibility = View.GONE
                binding?.layoutNoResult?.textViewError?.visibility = View.GONE
                val searchInfoText =
                    "${users.size} users found named '${searchView.query.ifEmpty { "Jim" }}'"
                viewModel.searchQuery.value = searchInfoText
                binding?.textViewSearchInfo?.text = viewModel.searchQuery.value

                rvUsers?.adapter = ListUserAdapter(users)
            }
        }
    }

    private fun setupUI() {
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun setupRecyclerView() {
        rvUsers = binding?.recyclerViewUsers

        rvUsers?.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupProgressBar() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding?.apply {
                content.isVisible = !isLoading
                layoutNoResult.textViewEmpty.isVisible = !isLoading
                layoutNoResult.textViewError.isVisible = !isLoading
                progressBar.isVisible = isLoading
            }
        }
    }
}