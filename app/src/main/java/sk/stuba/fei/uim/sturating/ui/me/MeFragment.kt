package sk.stuba.fei.uim.sturating.ui.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.fragment_me.view.*
import sk.stuba.fei.uim.sturating.R

class MeFragment : Fragment() {

    private lateinit var meViewModel: MeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        meViewModel =
            ViewModelProvider(this).get(MeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_me, container, false)

        val auth = FirebaseAuth.getInstance()
        val db = Firebase.database.reference

        db.child("users").child(auth.uid.toString()).child("name")

        return root
    }
        //tvMe = root.findViewById(R.id.tvMyProfile)
        //tvMe?.text = "asdasd"
        //meViewModel.text.observe(viewLifecycleOwner, {
        //    tvMe.text = it
        //})
//        root.btnWrite.setOnClickListener { write() }
//        val db = Firebase.database
//        val myRef = db.getReference("message")
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.getValue<String>()
//                tvMe?.text = value
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                //asd
//            }
//        })
//
//        return root

//    private fun write() {
//        val db = Firebase.database
//        val myRef = db.getReference("message")
//
//        myRef.setValue("Hello, World!")
}