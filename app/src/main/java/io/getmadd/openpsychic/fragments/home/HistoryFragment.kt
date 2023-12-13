package io.getmadd.openpsychic.fragments.home

import HistoryFragmentAdapter
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentHistoryBinding
import io.getmadd.openpsychic.model.History


class HistoryFragment : Fragment() {
    private lateinit var _binding: FragmentHistoryBinding
    private val binding get() = _binding
    var db = Firebase.firestore

    var userHistoryList = ArrayList<History>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var uid = Firebase.auth.uid
        
        var colRef = db.collection("history")


        colRef.whereIn("uID", listOf("$uid"))
            .get()
            .addOnSuccessListener { result ->

                if(result.isEmpty){
                    binding.emptyHistoryTextView.visibility = View.VISIBLE
                }
                else{
                    binding.emptyHistoryTextView.visibility = View.GONE
                    binding.historyRecyclerView.adapter!!.notifyDataSetChanged()
                }

                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                }
            }
            .addOnFailureListener { exception ->
            }

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = HistoryFragmentAdapter(
            userHistoryList
        ) {}

    }

}