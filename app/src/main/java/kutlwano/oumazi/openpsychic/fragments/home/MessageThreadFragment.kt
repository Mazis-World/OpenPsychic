package kutlwano.oumazi.openpsychic.fragments.home

import MessageThreadAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.databinding.FragmentMessageThreadBinding
import kutlwano.oumazi.openpsychic.model.Message
import kutlwano.oumazi.openpsychic.model.MessageMetaData
import kutlwano.oumazi.openpsychic.model.Psychic
import kutlwano.oumazi.openpsychic.services.UserPreferences


class MessageThreadFragment: Fragment() {
    private lateinit var binding: FragmentMessageThreadBinding
    var messagelist = ArrayList<Message>()
    var db = Firebase.firestore
    private var isPremium = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        binding = FragmentMessageThreadBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var bundle = arguments
        lateinit var psychic: Psychic
        lateinit var messagemetadata: MessageMetaData
        lateinit var receiverUserId: String
        var senderUserId = Firebase.auth.uid
        var userid = Firebase.auth.uid
        lateinit var userprofileimgsrc: String
        lateinit var username: String
        var prefs = UserPreferences(requireContext())
        var subscriptionstate = prefs.subscriptionstate
        var state = context?.let { UserPreferences(it).subscriptionstate }

        if(state == "active"){
            isPremium = true
        }

        if (bundle != null) {
            if (bundle.getSerializable("psychic") != null) {
                psychic = (bundle.getSerializable("psychic") as? Psychic)!!
                Glide.with(this)
                    .load(psychic.profileimgsrc)
                    .apply(RequestOptions.circleCropTransform())
                    .error(
                        Glide.with(this)
                            .load(R.drawable.openpsychiclogo)
                            .apply(RequestOptions.circleCropTransform())
                    )
                    .into(binding.messagethrheadimageview)
                binding.conversationwithtextview.text = "Chat With " + psychic.displayname
                receiverUserId = psychic.userid.toString()
            }
            if (bundle.getSerializable("messagemetadata") != null) {
                messagemetadata = (bundle.getSerializable("messagemetadata") as? MessageMetaData)!!
                receiverUserId = messagemetadata.receiverid.toString()
                senderUserId = messagemetadata.senderid
                psychic = Psychic(
                    userid = messagemetadata.receiverid,
                    displayname = messagemetadata.psychicdisplayname,
                    profileimgsrc = messagemetadata.psychicprofileimgsrc
                )
                userprofileimgsrc = messagemetadata.userprofileimgsrc.toString()
                username = messagemetadata.username.toString()
                val glideRequest = Glide.with(this)
                    .load(if (userid == senderUserId) messagemetadata.psychicprofileimgsrc else messagemetadata.userprofileimgsrc)
                    .apply(RequestOptions.circleCropTransform())
                    .error(
                        Glide.with(this)
                            .load(R.drawable.openpsychiclogo)
                            .apply(RequestOptions.circleCropTransform())
                    )

                glideRequest.into(binding.messagethrheadimageview)

                binding.conversationwithtextview.text = "Chat With ${
                    if (userid == senderUserId) messagemetadata.psychicdisplayname else "@${messagemetadata.username}"
                }"

            }
            if (bundle.get("usermetadata") != null) {
                messagemetadata = (bundle.getSerializable("usermetadata") as? MessageMetaData)!!
                userprofileimgsrc = messagemetadata.userprofileimgsrc.toString()
                username = messagemetadata.username.toString()
            }

            binding.messagethreadrecyclerview.adapter =
                MessageThreadAdapter(messagelist, messagemetadata) {}
            binding.messagethreadrecyclerview.layoutManager = LinearLayoutManager(context)

            val sendermessageref = db.collection("users")
                .document(senderUserId!!)
                .collection("messages")
                .document(receiverUserId).collection("messages")

            val receivermessageref = db.collection("users")
                .document(receiverUserId).collection("messages")
                .document(senderUserId).collection("messages")

            val sendermessagestampref = db.collection("users")
                .document(senderUserId).collection("messages")
                .document(receiverUserId)

            val receivermessagestampref = db.collection("users")
                .document(receiverUserId).collection("messages")
                .document(senderUserId)

            sendermessageref
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
                    (binding.messagethreadrecyclerview.adapter as? MessageThreadAdapter)?.apply {
                        messagelist.clear()
                        messagelist.addAll(messagesList)
                        notifyDataSetChanged()
                    }
                }

            var messageMap: MutableMap<String, Any?>

            binding.messagethreadinputlayout.setEndIconOnClickListener {

                if (binding.messagethreadedittext.text?.isNotEmpty() == true) {
                        var text = binding.messagethreadedittext.text
                        if (userid == senderUserId) {
                            messageMap = HashMap()
                            messageMap["senderid"] = userid
                            messageMap["receiverid"] = receiverUserId
                            messageMap["timestamp"] = Timestamp.now().toDate()
                            messageMap["message"] = "$text"
                            messageMap["status"] = "sent"
                        } else {
                            messageMap = HashMap()
                            messageMap["senderid"] = receiverUserId
                            messageMap["receiverid"] = userid
                            messageMap["timestamp"] = Timestamp.now().toDate()
                            messageMap["message"] = "$text"
                            messageMap["status"] = "sent"
                        }
                        val metadata = hashMapOf(
                            "senderid" to senderUserId,
                            "receiverid" to receiverUserId,
                            "psychicprofileimgsrc" to psychic.profileimgsrc,
                            "psychicdisplayname" to psychic.displayname,
                            "userprofileimgsrc" to userprofileimgsrc,
                            "username" to username,
                            "createdat" to Timestamp.now().toDate(),
                            "lastupdate" to Timestamp.now().toDate()
                        )
                        val updatemetadata = hashMapOf(
                            "senderid" to senderUserId,
                            "receiverid" to receiverUserId,
                            "psychicprofileimgsrc" to psychic.profileimgsrc,
                            "psychicdisplayname" to psychic.displayname,
                            "userprofileimgsrc" to userprofileimgsrc,
                            "username" to username,
                            "lastupdate" to Timestamp.now().toDate()
                        )

                        sendermessagestampref.get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document != null && document.exists()) {
                                        sendermessagestampref.update(updatemetadata as Map<String, Any>)
                                        receivermessagestampref.update(updatemetadata as Map<String, Any>)
                                    } else {
                                        sendermessagestampref.set(metadata)
                                        receivermessagestampref.set(metadata)
                                    }
                                }

                                sendermessageref.document().set(messageMap)
                                    .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                        override fun onComplete(task: Task<Void?>) {
                                            if (task.isSuccessful) {
                                                binding.messagethreadedittext.setText("")
                                            } else {
                                            }
                                        }
                                    })
                                receivermessageref.document().set(messageMap)
                                    .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                        override fun onComplete(task: Task<Void?>) {
                                            if (task.isSuccessful) {
                                                binding.messagethreadedittext.setText("")
                                            } else {
                                            }
                                        }
                                    })
                            }

                    }
                else{
                    Toast.makeText(activity,"You Must Say Something..", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}





