package io.getmadd.openpsychic.fragments.home

import MessageThreadAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentMessageThreadBinding
import io.getmadd.openpsychic.model.Message
import io.getmadd.openpsychic.model.Psychic


class MessageThreadFragment: Fragment(){
    private lateinit var binding: FragmentMessageThreadBinding
    var messagelist = ArrayList<Message>()
    var db = Firebase.firestore


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
        lateinit var message: Message
        lateinit var receiverUserId: String
        val senderUserId = Firebase.auth.uid

        if (bundle != null) {
            if(bundle.getSerializable("psychic") != null){
                psychic = (bundle.getSerializable("psychic") as? Psychic)!!
                Glide.with(this).load(psychic.profileimgsrc).into(binding.messagethrheadimageview)
                binding.conversationwithtextview.text = "Chat With " + psychic.displayname
                receiverUserId = psychic.userid
            }
            if(bundle.getSerializable("message") != null){
                message = (bundle.getSerializable("message") as? Message)!!
                binding.conversationwithtextview.text = "Chat With " + psychic.displayname
                receiverUserId = message.receiverid
            }


            binding.messagethreadrecyclerview.adapter = MessageThreadAdapter(messagelist) {}
            binding.messagethreadrecyclerview.layoutManager = LinearLayoutManager(context)

            val sendermessageref = db.collection("users")
                .document(senderUserId!!)
                .collection("messages")
                .document(receiverUserId).collection("messages")

            val receivermessageref = db.collection("users")
                .document(receiverUserId).collection("messages")
                .document(senderUserId).collection("messages")

            val sendermessagestampref = db.collection("users")
                .document(receiverUserId).collection("messages")
                .document(senderUserId)

            val receivermessagestampref = db.collection("users")
                .document(receiverUserId).collection("messages")
                .document(senderUserId)

            sendermessageref.addSnapshotListener { querySnapshot, exception ->
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

            binding.messagethreadinputlayout.setEndIconOnClickListener {
                if (binding.messagethreadedittext.text != null) {
                    var text = binding.messagethreadedittext.text
                    binding.messagethreadedittext.text =
                        Editable.Factory.getInstance().newEditable(" ")


                    val messageMap: MutableMap<String, Any?> = HashMap()
                    messageMap["senderid"] = senderUserId
                    messageMap["receiverid"] = receiverUserId
                    messageMap["timestamp"] = Timestamp.now().toDate()
                    messageMap["message"] = "$text"
                    messageMap["status"] = "sent"


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

        }

    }

}





