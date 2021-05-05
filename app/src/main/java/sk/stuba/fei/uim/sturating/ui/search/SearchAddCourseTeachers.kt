package sk.stuba.fei.uim.sturating.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.Course

class SearchAddCourseTeachers(
    private val teacherIds: List<Int>,
    private val course: Course
    ) : DialogFragment() {

    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private lateinit var spinnerLecturer: Spinner
    private lateinit var spinnerExaminer: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search_teacher_select, container, false)

        spinnerLecturer = root.findViewById(R.id.spinnerSearchSelectLecturer)
        spinnerExaminer = root.findViewById(R.id.spinnerSearchSelectExaminer)

        val teachers = mutableMapOf<Int, String>()
        // fills up the teacherIds-t
        teacherIds.forEach {
            db.child("teachers").child(it.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        teachers[it] = snapshot.child("name").value.toString()
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            teachers.values.toList()
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerLecturer.adapter = adapter
                        spinnerExaminer.adapter = adapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(error.message, "read failed on search addCourse select teacher")
                    }
                })
        }

        val btnSearchConfirmTeachers = root.findViewById<Button>(R.id.btnSearchConfirmTeachers)
        btnSearchConfirmTeachers.setOnClickListener {
            val userCourse = UserCourse(
                getKey(teachers, spinnerLecturer.selectedItem),
                getKey(teachers, spinnerExaminer.selectedItem),
                course.shortName
            )
            // Here can be set the maximum amount of courses
            saveUserCourse(userCourse)
        }

        return root
    }

    private fun saveUserCourse(userCourse: UserCourse) {
        val id = rand()
        db.child("users").child(auth.uid.toString())
            .child("courses").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.value == null) {
                        db.child("users").child(auth.uid.toString())
                            .child("courses")
                            .child(id)
                            .setValue(userCourse)
                        val parent = parentFragmentManager
                            .findFragmentByTag("Course Detailed Dialog Fragment") as DialogFragment
                        parent.dismiss()
                        dismiss()
                    } else {
                        saveUserCourse(userCourse)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "error on search add course select teacher")
                }
            })
    }

    private fun rand() = (0..100).random().toString()

    private fun <K, V> getKey(map: Map<K, V>, v: V): K =
        map.filter { v == it.value }.keys.first()

    private data class UserCourse(
        val lecturer_id: Int,
        val examiner_id: Int,
        val name: String
    )
}