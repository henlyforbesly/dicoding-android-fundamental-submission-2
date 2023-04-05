package com.example.submission.ui.favorite

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submission.data.network.responses.User
import com.example.submission.databinding.ActivityFavoriteBinding
import com.example.submission.ui.ViewModelFactory
import com.example.submission.ui.main.ListUserAdapter

class FavoriteActivity : AppCompatActivity() {

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private var _activityFavoriteBinding: ActivityFavoriteBinding? = null
    private val binding get() = _activityFavoriteBinding
    private var rvUsers: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        setupProgressBar()
        setupRecyclerView()
        observeData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityFavoriteBinding = null
    }

    private fun setupUI() {
        _activityFavoriteBinding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Favorite Users"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupProgressBar() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding?.apply {
                content.isVisible = !isLoading
                layoutNoResult.textViewError.isVisible = !isLoading
                layoutNoResult.textViewEmpty.isVisible = !isLoading
                progressBar.isVisible = isLoading
            }
        }
    }

    private fun setupRecyclerView() {
        rvUsers = binding?.recyclerViewUsers

        rvUsers?.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeData() {
        viewModel.getAllFavorites().observe(this) { users ->
            if (users.isEmpty()) {
                binding?.content?.isVisible = false
                binding?.layoutNoResult?.textViewEmpty?.visibility = View.VISIBLE
            } else {
                binding?.content?.isVisible = true
                binding?.layoutNoResult?.textViewEmpty?.visibility = View.GONE
                binding?.layoutNoResult?.textViewError?.visibility = View.GONE
                val listOfUsers = arrayListOf<User>()
                users.map {
                    listOfUsers.add(User(
                        it.login,
                        it.avatarUrl,
                    ))
                }

                rvUsers?.adapter = ListUserAdapter(listOfUsers)
            }
        }
    }
}