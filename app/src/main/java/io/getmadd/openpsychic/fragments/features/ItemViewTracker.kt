package io.getmadd.openpsychic.fragments.features

import Dream
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ItemViewTracker(private val recyclerView: RecyclerView, dreams: MutableList<Dream>) : RecyclerView.OnScrollListener() {
    private val viewedItems = mutableSetOf<Int>()
    private val dreams = dreams

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                if (!viewedItems.contains(i)) {
                    // Item i is now visible, mark it as viewed
                    viewedItems.add(i)
                    incrementItemView(dreams[i])
                }
            }
        }
    }

    private fun incrementItemView(dream: Dream) {
        val db = FirebaseFirestore.getInstance()
        val itemRef = db.collection("dreamPost").document(dream.dreamId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(itemRef)
            val currentViews = snapshot.getLong("views") ?: 0
            transaction.update(itemRef, "views", currentViews + 1)
        }.addOnSuccessListener {
            Log.d("ItemViewTracker", "View count incremented for item: ${dream.dreamId}")
        }.addOnFailureListener { e ->
            Log.e("ItemViewTracker", "Failed to increment view count for item: ${dream.dreamId}", e)
        }
    }
}
