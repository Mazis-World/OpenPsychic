package io.getmadd.openpsychic.fragments.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.adapters.PaymentMethodAdapter
import io.getmadd.openpsychic.databinding.FragmentAccountBinding
import io.getmadd.openpsychic.model.PaymentMethod
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.services.UserPreferences
import java.util.Calendar

class AccountFragment: Fragment() {

    private lateinit var _binding:FragmentAccountBinding
    private val binding get() = _binding
    private var db = Firebase.firestore
    private lateinit var prefs: UserPreferences
    private var selectedCategory: String? = null
    private var origincountry: String? = null
    private var psychicondisplay = false
    private lateinit var psychicondisplaycategory: String
    private var psychic = Psychic()
    var listpaymentmethods = ArrayList<PaymentMethod>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = UserPreferences(context!!)
        setupUI()
        if(prefs.usertype == "psychic") {
            userPsychic()
        }
    }

    fun userPsychic(){
            db.collection("users").document(prefs.uid!!)
                .get()
                .addOnSuccessListener { result ->
                    psychic = result.toObject(Psychic::class.java)!!
                    val psychicondisplay = result.getBoolean("psychicondisplay")!!
                    val psychicondisplaycategory = result.getString("psychicondisplaycategory")
                        .toString()

                    if (psychicondisplay) {
                        selectedCategory = psychic.psychicondisplaycategory
                        binding.psychicOnDisplaySwitch.isChecked = true
                        binding.categorySpinner.isEnabled = false
                        categorySpinnerList(psychicondisplaycategory)
                    }

                    if(psychic.psychicoriginyear != null){
                        binding.originyearet.setText( psychic.psychicoriginyear.toString())
                        binding.originyearet.isEnabled = false
                        binding.yearoriginupdatebutton.isEnabled = false
                        binding.yearoriginupdatebutton.setTextColor(resources.getColor(R.color.op_grey))
                    }

                    if(psychic.psychicorigincountry != null){
                        val countryArray = resources.getStringArray(R.array.country_names)
                        val position = countryArray.indexOf(psychic.psychicorigincountry)
                        binding.categorySpinner2.isEnabled = false
                        binding.categorySpinner2.setSelection(position)
                        binding.countryoriginupdatebutton.isEnabled = false
                        binding.countryoriginupdatebutton.setTextColor(resources.getColor(R.color.op_grey))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }

        binding.paymentmethodrecyclerview.layoutManager = LinearLayoutManager(context)
        binding.paymentmethodrecyclerview.adapter = PaymentMethodAdapter(items = listpaymentmethods) {}

        db.collection("users").document(prefs.uid!!).collection("paymentmethods")
            .get()
            .addOnSuccessListener { result ->
                for(result in result){
                    var method = result.toObject(PaymentMethod::class.java)
                    listpaymentmethods.add(method)
                }
                binding.paymentmethodrecyclerview.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }


    }

    private fun setupUI(){

        binding.psychicOnDisplaySwitch.setOnCheckedChangeListener { _, isChecked ->
            handlePsychicOnDisplaySwitchChanged(isChecked)
        }

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.categorySpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                origincountry = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.yearoriginupdatebutton.setOnClickListener {
            val year = binding.originyearet.text.toString().toIntOrNull()
            if(!psychicondisplay){
                if(year !=  null && isYearValid(year)){
                    db.collection("users").document(prefs.uid!!).update("psychicoriginyear",year)
                }
                else Toast.makeText(context, "Invalid Year", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Cannot Update While Psychic On Display", Toast.LENGTH_SHORT).show()
            }
        }
        binding.countryoriginupdatebutton.setOnClickListener {
            if(!psychicondisplay) {
                if (origincountry != null) {
                    db.collection("users").document(prefs.uid!!)
                        .update("psychicorigincountry", origincountry)
                    binding.countryoriginupdatebutton.isEnabled = false
                    binding.countryoriginupdatebutton.isEnabled = false
                    binding.categorySpinner2.isEnabled = false
                    binding.countryoriginupdatebutton.setTextColor(resources.getColor(R.color.op_grey))
                } else {
                    Toast.makeText(context, "Invalid Country", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(context, "Cannot Update While Psychic On Display", Toast.LENGTH_SHORT).show()
            }
        }

        val usertype = prefs.usertype

        if(usertype.equals( "psychic")){
            binding.psychicsettingslayout.setVisibility(View.VISIBLE)
        }
        else binding.psychicsettingslayout.visibility = View.GONE

        Log.e("AccountFragment", prefs.usertype!!)
        binding.usernameEditText.setText(prefs.username)
        binding.displaynameedittext.setText(prefs.displayname)
        binding.biographytextview.text = prefs.bio
        binding.emailedittext.text = prefs.email

        binding.nameupdatebutton.setOnClickListener {
            if(psychicondisplay){
                Toast.makeText(context, "Psychic On Display, Cannot Update", Toast.LENGTH_SHORT).show()
            }
            else if(prefs.username != binding.usernameEditText.text.toString() && binding.usernameEditText.text != null){
                db.collection("users").document(prefs.uid!!).update("username", binding.usernameEditText.text.toString()).addOnSuccessListener {
                    prefs.username = binding.usernameEditText.text.toString()
                    Toast.makeText(context, " Username Updated", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(context, "Invalid Username", Toast.LENGTH_SHORT).show()
            }
        }
        binding.displaynameupdatebutton.setOnClickListener {
            if(psychicondisplay){
                Toast.makeText(context, "Psychic On Display, Cannot Update", Toast.LENGTH_SHORT).show()
            }
            else if(prefs.displayname != binding.displaynameedittext.text.toString() && binding.displaynameedittext.text != null){
                db.collection("users").document(prefs.uid!!).update("displayname", binding.displaynameedittext.text.toString())
                prefs.displayname = binding.displaynameedittext.text.toString()
                Toast.makeText(context, "Display Name Updated", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, "Invalid Display Name", Toast.LENGTH_SHORT).show()
            }
        }
        binding.biographylayoutbutton.setOnClickListener{
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_bio, null)

            val bioEditText = dialogView.findViewById<EditText>(R.id.paymentprovideredittext)
            val updateButton = dialogView.findViewById<Button>(R.id.updateButton)

            val builder = AlertDialog.Builder(context!!)
                .setView(dialogView)
            val dialog = builder.create()

            if(psychicondisplay){
                Toast.makeText(context, "Psychic On Display, Cannot Update", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(prefs.bio != " ") {
                bioEditText.setText(prefs.bio)
            }
            dialog.show()

            updateButton.setOnClickListener {
                val updatedBio = bioEditText.text.toString()
                if(prefs.bio != updatedBio && updatedBio != " "){
                    db.collection("users").document(prefs.uid!!).update("bio",updatedBio)
                    prefs.bio = updatedBio
                    binding.biographytextview.text = updatedBio
                }
                else {
                    Toast.makeText(context, "Invalid Biography", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }
        binding.addpaymentmethodlayoutbutton.setOnClickListener{
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_payment_add, null)

            val updatebutton = dialogView.findViewById<Button>(R.id.addpaymentupdatebutton)
            val provider = dialogView.findViewById<EditText>(R.id.paymentprovideredittext)
            val address = dialogView.findViewById<EditText>(R.id.paymentaddresset)

            val builder = AlertDialog.Builder(context!!)
                .setView(dialogView)
            val dialog = builder.create()

            updatebutton.setOnClickListener {
                val method = PaymentMethod()

                if(provider.text.isNotEmpty() && address.text.isNotEmpty()){
                    method.provider = provider.text.toString()
                    method.address = address.text.toString()

                    // Update the payment method in the database
                    val paymentMethodsRef = db.collection("users").document(prefs.uid!!).collection("paymentmethods")
                    paymentMethodsRef.get().addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            // Delete existing payment method
                            paymentMethodsRef.document(doc.id).delete()
                        }
                        // Set the new payment method
                        paymentMethodsRef.add(method).addOnCompleteListener{
                            Toast.makeText(context ,"Payment Method Updated!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    dialog.dismiss()
                }
            }

            dialog.show()
        }
    }

    fun isYearValid(year: Int): Boolean {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val earliestValidYear = 1900 // Adjust this based on your requirements
        val latestValidYear = currentYear // Adjust this based on your requirements
        return year in earliestValidYear..latestValidYear
    }

    private fun handlePsychicOnDisplaySwitchChanged(isChecked: Boolean) {
        if (isChecked) {
            addPsychicToDatabase()
            binding.categorySpinner.isEnabled = false
        } else {
            removePsychicFromDatabase()
            binding.categorySpinner.isEnabled = true
        }
    }

    private fun addPsychicToDatabase() {
        psychicondisplay = true
        psychic.psychicondisplay = true
        psychic.psychicondisplaycategory = selectedCategory!!

        db.collection("psychicOnDisplay")
            .document(selectedCategory!!)
            .collection("psychicsOnDisplay")
            .document(prefs.uid!!)
            .set(psychic)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "User added as a psychic with category: $selectedCategory")
                db.collection("users").document(prefs.uid!!).update("psychicondisplay", true, "psychicondisplaycategory", selectedCategory)
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error adding user as a psychic: $e")
            }
    }

    private fun categorySpinnerList(selectedcategory:String){
        val psychicCategories = listOf(
            "Past Lives",
            "Dream Interpretations",
            "Love & Relationships",
            "Fortune Telling",
            "Palm Readings",
            "Astrology",
            "Lingering Spirits",
            "Tarot Readings",
            "General Readings"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, psychicCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter
        val position = adapter.getPosition(selectedCategory)
        binding.categorySpinner.setSelection(position)
    }

    private fun removePsychicFromDatabase() {
       psychicondisplay = false
        selectedCategory.let {
            if (it != null) {
                db.collection("psychicOnDisplay")
                    .document(it)
                    .collection("psychicsOnDisplay")
                    .document(prefs.uid!!)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "User removed from the psychic database")
                        db.collection("users").document(prefs.uid!!).update("psychicondisplay", false, "psychicondisplaycategory", " ")
                    }
                    .addOnFailureListener { e ->
                        Log.e(ContentValues.TAG, "Error removing user from the psychic database: $e")
                    }
            }
        }
    }
}