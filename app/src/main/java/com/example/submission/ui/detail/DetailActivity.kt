package com.example.submission.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.submission.R
import com.example.submission.data.local.entities.User
import com.example.submission.databinding.ActivityDetailBinding
import com.example.submission.shared.Constants.Companion.GLIDE_HEIGHT_DETAIL_ACTIVITY
import com.example.submission.shared.Constants.Companion.GLIDE_WIDTH_DETAIL_ACTIVITY
import com.example.submission.ui.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    private var _activityDetailBinding: ActivityDetailBinding? = null
    private val binding get() = _activityDetailBinding

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        setupProgressBar()
        setupFollowPager()
        setupErrorPage()

        if (savedInstanceState === null) {
            fetchData()
        }

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
        _activityDetailBinding = null
    }

    private fun setupErrorPage() {
        viewModel.isError.observe(this) { isError ->
            binding?.apply {
                content.isVisible = !isError
                layoutNoResult.textViewError.isVisible = isError
            }
        }
    }

    private fun setupFollowPager() {
        val followPagerAdapter = FollowPagerAdapter(this)
        user?.let {
            followPagerAdapter.username = it.login
        }

        val viewPager: ViewPager2? = binding?.viewPagerFollow
        viewPager?.adapter = followPagerAdapter

        val tabs: TabLayout? = binding?.tabLayoutFollow
        if (tabs != null) {
            if (viewPager != null) {
                TabLayoutMediator(tabs, viewPager) { tab, position ->
                    tab.text = resources.getString(TAB_TITLES[position])
                }.attach()
            }
        }
    }

    private fun fetchData() {
        user?.let { viewModel.getUserDetail(it.login) }
    }

    private fun observeData() {
        viewModel.userDetail.observe(this) {
            val (bio, publicRepos, followers, avatarUrl, following, name, location) = it

            binding?.layoutNoResult?.textViewEmpty?.visibility = View.GONE
            binding?.layoutNoResult?.textViewError?.visibility = View.GONE

            Glide.with(this)
                .load(avatarUrl)
                .override(GLIDE_WIDTH_DETAIL_ACTIVITY, GLIDE_HEIGHT_DETAIL_ACTIVITY)
                .circleCrop()
                .into(binding?.imageViewAvatar as ImageView)
            binding?.textViewName?.text = name
            location?.let {
                val locationText = "📍 $location"
                binding?.textViewLocation?.text = locationText
            }
            if (bio == null) {
                binding?.textViewBio?.visibility = View.GONE
            } else {
                binding?.textViewBio?.text = bio
            }
            binding?.textViewRepos?.text = publicRepos.toString()
            binding?.textViewFollowersNumber?.text = followers.toString()
            binding?.textViewFollowingNumber?.text = following.toString()
        }

        user?.let { user ->
            viewModel.checkIsFavorited(user.login).observe(this) { userData ->
                if (userData != null) {
                    binding?.floatingActionButtonIsFavorite?.apply {
                        setImageResource(R.drawable.baseline_favorite_24)
                        setOnClickListener { viewModel.deleteUserFromFavorite(user) }
                    }
                } else {
                    binding?.floatingActionButtonIsFavorite?.apply {
                        setImageResource(R.drawable.baseline_favorite_border_24)
                        setOnClickListener { viewModel.insertUserAsFavorite(user) }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        _activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        user = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_USER, User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_USER)
        }
        supportActionBar?.title = user?.login
    }

    private fun setupProgressBar() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding?.apply {
                content.isVisible = !isLoading
                layoutNoResult.textViewError.isVisible = !isLoading
                progressBar.isVisible = isLoading
            }
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers_tab_text,
            R.string.following_tab_text,
        )
    }
}