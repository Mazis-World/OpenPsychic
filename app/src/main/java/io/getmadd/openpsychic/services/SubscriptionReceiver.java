package io.getmadd.openpsychic.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SubscriptionReceiver extends BroadcastReceiver implements PurchasesUpdatedListener {

    private static final String TAG = "SubscriptionReceiver";
    private BillingClient billingClient;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        Log.e(TAG, "Received action: " + action);

        if ("com.android.vending.RECEIVE_SUBSCRIPTION_CHANGED".equals(action)) {
            // Extract subscription information
            Bundle extras = intent.getExtras();
            if (extras != null) {
                boolean isAutoRenewing = extras.getBoolean("autoRenewing", true); // Default to true if not found
                long expirationTimeMillis = extras.getLong("expirationTimeMillis", -1); // Default to -1 if not found
                String orderId = extras.getString("orderId", "");
                long currentTimeMillis = System.currentTimeMillis();
                long thirtyOneDaysInMillis = 31L * 24L * 60L * 60L * 1000L; // 31 days in milliseconds

                if (!isAutoRenewing || expirationTimeMillis == 0L) {
                    // Subscription has been cancelled or expired
                    updateSubscriptionStateInDatabase(context, false, orderId, currentTimeMillis, "expired");
                    new UserPreferences(context).saveSubscription("expired");
                } else {
                    // Subscription is active and auto-renewing
                    updateSubscriptionStateInDatabase(context, true, orderId, currentTimeMillis + thirtyOneDaysInMillis, "active");
                    new UserPreferences(context).saveSubscription("active");
                }
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.w(TAG, "User canceled the purchase");
        } else {
            Log.e(TAG, "Error handling purchase update: " + billingResult.getDebugMessage());
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                // Acknowledge the purchase
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Purchase acknowledged");
                        // Update subscription state in database
                        long thirtyOneDaysInMillis = 31L * 24L * 60L * 60L * 1000L; // 31 days in milliseconds
                        updateSubscriptionStateInDatabase(context, true, purchase.getOrderId(), purchase.getPurchaseTime() + thirtyOneDaysInMillis, "active");
                    } else {
                        Log.e(TAG, "Error acknowledging purchase: " + billingResult.getDebugMessage());
                    }
                });
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            Log.d(TAG, "Purchase is pending");
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            Log.d(TAG, "Purchase state is unspecified");
        }
    }

    private void updateSubscriptionStateInDatabase(Context context, boolean isPremium, String orderId, long endDate, String state) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            SubscriptionData subscriptionData = new SubscriptionData(isPremium, endDate, state);

            db.collection("subscriptions").document(uid)
                    .collection("subscriptions").document(orderId).set(subscriptionData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Subscription state updated successfully in subscriptions collection"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating subscription state in subscriptions collection", e));

            db.collection("users").document(uid)
                    .collection("subscriptions").document(orderId).set(subscriptionData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Subscription state updated successfully in users collection"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating subscription state in users collection", e));

            // Also update the isPremium field in the user's document
            db.collection("users").document(uid).update("isPremium", isPremium)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User's premium status updated successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating user's premium status", e));
        }
    }

    private static class SubscriptionData {
        boolean autorenewing;
        long enddate;
        String state;

        SubscriptionData(boolean autorenewing, long enddate, String state) {
            this.autorenewing = autorenewing;
            this.enddate = enddate;
            this.state = state;
        }
    }
}
