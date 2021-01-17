package sk.stuba.fei.uim.sturating.ui.me

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import sk.stuba.fei.uim.sturating.R
import kotlin.math.floor

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
        read("name", root.findViewById(R.id.tvName))
        read("email", root.findViewById(R.id.tvEmail))
        read("no_rat_cour", root.findViewById(R.id.tvNoRatedCour))
        read("no_rat_exam", root.findViewById(R.id.tvNoRatedExam))
        read("no_rat_lect", root.findViewById(R.id.tvNoRatedLec))
        readStars("avg_rat_cour", root.findViewById(R.id.tvUsrAvgRatCourse))
        readStars("avg_rat_exam",root.findViewById(R.id.tvUsrAvgRatExam))
        readStars("avg_rat_lect", root.findViewById(R.id.tvUsrAvgRatLec))

        return root
    }

    private fun read(type: String, tv: TextView) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tv.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Failed to read$type", error.toException())
            }
        }

        val auth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().reference.child("users")
            .child(auth.uid.toString()).child(type)
        ref.keepSynced(true)
        ref.addValueEventListener(listener)
    }

    private fun readStars(type: String, tv: TextView) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.getValue<Double>()?.let { floor(it) }?.toInt() ?: 0

                var result = ""
                for (i in 1..count) {
                    result += "⭐"
                }

                tv.text = result
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Failed to read$type", error.toException())
            }
        }

        val auth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().reference.child("users")
            .child(auth.uid.toString()).child(type)
        ref.keepSynced(true)
        ref.addValueEventListener(listener)
    }
}