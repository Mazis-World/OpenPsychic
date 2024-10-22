package kutlwano.oumazi.openpsychic.fragments.home

import RequestMessagesAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.databinding.FragmentRequestHistoryViewBinding
import kutlwano.oumazi.openpsychic.model.Message
import kutlwano.oumazi.openpsychic.model.Psychic
import kutlwano.oumazi.openpsychic.model.Request
import kutlwano.oumazi.openpsychic.model.RequestStatusUpdate

class RequestHistoryView : Fragment() {
    private lateinit var binding: FragmentRequestHistoryViewBinding
    private var userid = Firebase.auth.uid
    private var usertype = "null"
    private var db = Firebase.firestore
    lateinit var request: Request
    var messagelist = ArrayList<Message>()

    lateinit var userrequestfef: DocumentReference
    lateinit var psychicrequestref: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        binding = FragmentRequestHistoryViewBinding.inflate(inflater, container, false)
        val bundle = arguments
        var userPsychic: Psychic

        if (bundle != null) {

            request = (bundle.getSerializable("request") as? Request)!!
            // Now you have access to the Psychic object in the Fragment
            binding.timestamptextview.text = request.timestamp.toDate().toString()
            binding.statustextview.text = request.requeststatus
            binding.fullnametextview.text = "Full Name: "+request.fullName
            binding.subjecttextview.text = "Subject: " + request.specificQuestion
            binding.energyfocustextview.text = "Energy Focus: " + request.energyFocus
            binding.preferredreadingmethodtextview.text =
                "Reading Method: " + request.preferredReadingMethod
            binding.opentoinsightstextview.text = "Open To Insights: ${if (request.openToInsights) "Y" else "N"}"
            binding.requestmessagetextview.text = "Message: " + request.message
        }

        db.collection("users").document(userid!!).get()
            .addOnSuccessListener { result ->
                usertype = result.getString("usertype").toString()
                requeststatuslistener()
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userrequestfef = db.collection("users").document(request.senderid).collection("request").document(request.receiverid).collection("status").document()
        psychicrequestref = db.collection("users").document(request.receiverid).collection("request").document(request.senderid).collection("status").document()
        var requeststatus = RequestStatusUpdate()

        binding.requestacceptbutton.setOnClickListener{

            requeststatus.status = "accepted"
            requeststatus.timestamp = Timestamp.now()
            userrequestfef.set(requeststatus)
            psychicrequestref.set(requeststatus)
            binding.psychicRequestResponseLayout.visibility = View.GONE
            binding.statuscupdatelayout.visibility = View.VISIBLE
        }
        binding.requestdenybutton.setOnClickListener{
            requeststatus.status = "denied"
            requeststatus.timestamp = Timestamp.now()
            userrequestfef.set(requeststatus)
            psychicrequestref.set(requeststatus)
            binding.psychicRequestResponseLayout.visibility = View.GONE
            binding.statuscupdatelayout.visibility = View.VISIBLE
        }

        binding.requestreplyinputlayout.setEndIconOnClickListener{
            var userrequestref = db.collection("users").document(request.senderid).collection("request").document(request.receiverid).collection("messages")
            var psychicrequestref = db.collection("users").document(request.receiverid).collection("request").document(request.senderid).collection("messages")
            var message = Message()
            if(binding.replyinputedittext.text!!.isNotEmpty()){
                message.status = "sent"
                message.message = binding.replyinputedittext.text.toString()
                message.senderid = userid.toString()
                message.receiverid = " "
                message.timestamp = Timestamp.now()
                psychicrequestref.document().set(message)
                userrequestref.document().set(message)
                binding.replyinputedittext.setText(" ")
            }
        }

        binding.closerequesttextview.setOnClickListener{
            AlertDialog.Builder(requireActivity(), R.style.AlertDialogCustom)
                .setTitle("Are you sure you'd like to close this request?")
                .setPositiveButton("CLOSE",) { _, _ ->
                    requeststatus.status = "closed"
                    requeststatus.timestamp = Timestamp.now()
                    userrequestfef.set(requeststatus)
                    psychicrequestref.set(requeststatus)
                }
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.cancel()
                }
                .create().show()
        }

        binding.requestrepliesrecyclerview.adapter = RequestMessagesAdapter(messagelist,{})
        binding.requestrepliesrecyclerview.layoutManager = LinearLayoutManager(context)

        messageslistener()
    }

    fun messageslistener(){
        var messagesref = db.collection("users").document(request.senderid).collection("request").document(request.receiverid).collection("messages")
        var psychicmessagesref = db.collection("users").document(request.receiverid).collection("request").document(request.senderid).collection("messages")

        messagesref
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    // Handle the exception
                    return@addSnapshotListener
                }

                val messagesList = mutableListOf<Message>()
                for (document in querySnapshot!!.documents) {
                    val message = document.toObject(Message::class.java)
                    if (message != null) {
                        messagesList.add(message)
                    }
                }
                (binding.requestrepliesrecyclerview.adapter as? RequestMessagesAdapter)?.apply {
                    messagelist.clear()
                    messagelist.addAll(messagesList)
                    notifyDataSetChanged()
                }
            }    }

    fun requeststatuslistener(){
        var listenerRegistration: ListenerRegistration? = null
        var userrequestfef = db.collection("users").document(request.receiverid).collection("request").document(request.senderid).collection("status").orderBy("timestamp")
        var  psychicrequestref = db.collection("users").document(request.senderid).collection("request").document(request.receiverid).collection("status").orderBy("timestamp").orderBy("timestamp")

        lateinit var requestref: Query

        fun startListeningToField(fieldName: String, callback: (RequestStatusUpdate) -> Unit) {

            requestref = if (usertype == "user"){
                userrequestfef
            } else{
                psychicrequestref
            }

            listenerRegistration = requestref.addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    // Handle errors
                    return@addSnapshotListener
                }

                if (documentSnapshot != null) {
                    Log.e("status", documentSnapshot.size().toString())
                }


                if (documentSnapshot != null && !documentSnapshot.isEmpty) {
                    Log.e("status", documentSnapshot.size().toString())
                    var statusupdate = RequestStatusUpdate()
                    statusupdate.status = documentSnapshot.documents.last().data?.get("status").toString()
                    statusupdate.timestamp = documentSnapshot.documents.last().data?.get("timestamp") as Timestamp

                    callback.invoke(statusupdate)
//                    } else {
//                        // Handle the case where the field doesn't exist in the document
//                    }
                } else {
                    var statusupdate = RequestStatusUpdate()
                    statusupdate.status = "sent"
                    callback.invoke(statusupdate)
                    // Handle the case where the document doesn't exist
                }
            }
        }

        startListeningToField("status") {
            Log.e("status", it.status)
            view?.let { it1 -> statusupdateview(it.status,it.timestamp , it1) }
        }
    }

    fun statusupdateview(requeststatus: String, statustimestamp: Timestamp, viewBinding:View  ){
        val statuslayout = viewBinding.findViewById<LinearLayout>(R.id.psychic_request_response_layout)
        val requestinputlayout = viewBinding.findViewById<LinearLayout>(R.id.requestreplyinputlayout)
        val statusupdatelayout = viewBinding.findViewById<LinearLayout>(R.id.statuscupdatelayout)
        val repliesrecycler = viewBinding.findViewById<RecyclerView>(R.id.requestrepliesrecyclerview)
        val statusupdatetextview = viewBinding.findViewById<TextView>(R.id.statusupdatetextview)
        val statusupdatetimestamptextview = viewBinding.findViewById<TextView>(R.id.statustimestamp)
        val closerequesttextview = viewBinding.findViewById<TextView>(R.id.closerequesttextview)

        Log.e("status", usertype)

        if((usertype == "psychic" && requeststatus == "sent") || (usertype == "psychic" && requeststatus == "created")){
            statuslayout.visibility = View.VISIBLE
        }
        if(requeststatus == "created" || requeststatus == "sent"){
            statusupdatetextview.text = "Request Created"
        }
        if(requeststatus == "accepted"){
            statuslayout.visibility = View.GONE
            requestinputlayout.visibility = View.VISIBLE
            statusupdatelayout.visibility = View.VISIBLE
            closerequesttextview.visibility = View.VISIBLE
            statusupdatetextview.text = "Request Accepted"
        }
        if(requeststatus == "denied"){
            statusupdatelayout.visibility = View.VISIBLE
            statusupdatetextview.text = "Request Denied"
        }
        if(requeststatus == "closed"){
            closerequesttextview.visibility = View.GONE
            requestinputlayout.visibility = View.GONE
            statusupdatetextview.text = "Request Closed"
            requestClosedRateReviewPsychic()
        }
        statusupdatetimestamptextview.text = statustimestamp.toDate().toString()
    }

    fun requestClosedRateReviewPsychic() {
        var userreview = db.collection("users").document(request.receiverid).collection("request").document(request.senderid).collection("review")

        if (usertype == "user") {
            userreview.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (documentSnapshot == null || documentSnapshot.isEmpty) {
                        // The collection doesn't exist or is empty
                        // Show your dialog here
                        val dialog = RatingDialogFragment(request)
                        dialog.show(fragmentManager!!,"RequestReviewDialog")
                    }
                } else {
                    // review submitted
                    // An error occurred while fetching the collection
                    // Handle the error as needed
                    // task.exception?.let { handleException(it) }
                }
            }
        }
    }
}
