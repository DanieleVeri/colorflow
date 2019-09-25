package com.colorflow

import android.app.Activity
import android.os.Handler
import android.os.Message
import com.colorflow.ads.IAdHandler
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G

class AdHandler(private val activity: Activity): Handler(), IAdHandler {
    private lateinit var rewarded_ad: RewardedAd
    private var earned = false
    private var pending_start = false
    private var is_loading = false

    init {
        val configuration = MobileAds.getRequestConfiguration().toBuilder()
                .setMaxAdContentRating(MAX_AD_CONTENT_RATING_G)
                .build()
        MobileAds.setRequestConfiguration(configuration)
        sendEmptyMessage(MessageType.LOAD_AD.code())
    }

    override fun show_ad() {
        sendEmptyMessage(MessageType.SHOW_AD.code())
        earned = false
    }

    override fun is_rewarded(): Boolean {
        return earned
    }

    override fun handleMessage(message: Message) {
        synchronized(this@AdHandler) {
            when (message.what) {
                MessageType.SHOW_AD.code() -> {
                    if (!rewarded_ad.isLoaded) {
                        pending_start = true
                        if(!is_loading)
                            sendEmptyMessage(MessageType.LOAD_AD.code())
                        return
                    }
                    rewarded_ad.show(activity, ad_reward_cb)
                }
                MessageType.LOAD_AD.code() -> {
                    if (is_loading) return
                    is_loading = true
                    rewarded_ad = RewardedAd(activity, "ca-app-pub-3940256099942544/5224354917")
                    rewarded_ad.loadAd(AdRequest.Builder().build(), ad_load_cb)
                }
            }
        }
    }

    private val ad_load_cb = object : RewardedAdLoadCallback() {
        override fun onRewardedAdLoaded() {
            synchronized(this@AdHandler) {
                is_loading = false
                if (pending_start)
                    show_ad()
                pending_start = false
            }

        }

        override fun onRewardedAdFailedToLoad(errorCode: Int) {
            is_loading = false
            // display Ad failed to load.
        }
    }

    private val ad_reward_cb = object: RewardedAdCallback() {
        override fun onRewardedAdOpened() {}

        override fun onRewardedAdClosed() {
            sendEmptyMessage(MessageType.LOAD_AD.code())
        }

        override fun onUserEarnedReward(p0: com.google.android.gms.ads.rewarded.RewardItem) {
            // user reward
            earned = true
        }

        override fun onRewardedAdFailedToShow(errorCode: Int) {
            // Display Ad failed to display
        }
    }

    enum class MessageType {
        SHOW_AD { override fun code() = 1 },
        LOAD_AD { override fun code() = 0 };
        abstract fun  code(): Int
    }
}