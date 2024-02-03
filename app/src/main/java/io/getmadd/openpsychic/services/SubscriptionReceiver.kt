package io.getmadd.openpsychic.services
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

// Not implemented yet, will keep state of purchases

class SubscriptionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if ("com.android.vending.RECEIVE_SUBSCRIPTION_CHANGED" == action) {
            // Extract subscription information
            val extras: Bundle? = intent.extras
            extras?.let {
                val purchaseToken = it.getString("purchaseToken")
                val isAutoRenewing = it.getBoolean("autoRenewing")
                val expirationTimeMillis = it.getLong("expirationTimeMillis")

                // Determine subscription status based on your logic
                if (isAutoRenewing) {
                    // Subscription is active or renewed
                    // Check expirationTimeMillis for the next renewal date
                    Log.d("Subscription", "Active or Renewed")
                    updateSubscriptionStateInDatabase(context, "Active")
                } else {
                    // Subscription has been cancelled
                    if (expirationTimeMillis == 0L) {
                        Log.d("Subscription", "Cancelled")
                        updateSubscriptionStateInDatabase(context, "Cancelled")
                    } else {
                        // Subscription is not auto-renewing, but it's not necessarily cancelled
                        // It might be in a grace period, etc.
                        Log.d("Subscription", "Not Auto-Renewing (Grace Period?)")
                        updateSubscriptionStateInDatabase(context, "Not Auto-Renewing")
                    }
                }
            }
        }
    }

    private fun updateSubscriptionStateInDatabase(context: Context, newState: String) {
        // Perform database update based on the new subscription state
        // This is where you would update your local database or perform other actions
        // For simplicity, let's assume you have a DatabaseHelper class with an updateSubscriptionState method
    }
}
