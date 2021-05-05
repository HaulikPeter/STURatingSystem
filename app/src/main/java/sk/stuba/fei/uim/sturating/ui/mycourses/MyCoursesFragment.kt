package sk.stuba.fei.uim.sturating.ui.mycourses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.*
import sk.stuba.fei.uim.sturating.ui.search.SearchFragment

class MyCoursesFragment : Fragment(), CourseViewAdapter.CourseItemClickListener {

    private lateinit var adapter: CourseViewAdapter
    private lateinit var rvCourses: RecyclerView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private val courses = mutableListOf<Course>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_courses_filled, container, false)
        rvCourses = root.findViewById(R.id.rvCourses)
        adapter = CourseViewAdapter()
        adapter.itemClickLister = this
        readCourses()
        rvCourses.adapter = adapter

        root.findViewById<Button>(R.id.btnMyCoursesGotoSearch).setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            val fragment = SearchFragment()
            container?.id?.let { id -> transaction.replace(id, fragment) }
            transaction.addToBackStack(null)

            val nav = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
            nav?.selectedItemId = R.id.navigation_search
            activity?.supportFragmentManager?.popBackStack()

            transaction.commit()
        }

        return root
    }

    private fun readCourses() {
        db.child("users").child(auth.uid.toString()).child("courses")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //courseNames[it.key?.toInt() ?: -1] = it.child("name").toString()
                        courses.add(Course(
                            id = it.key?.toInt() ?: -1,
                            shortName = it.child("name").value.toString(),
                            courseLecturerId = it.child("lecturer_id").value.toString().toInt(),
                            courseExaminerId = it.child("examiner_id").value.toString().toInt()
                        ))
                    }
                    loadCourses()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "myCoursesFragment readCourses event fail")
                }
            })
    }

    private fun loadCourses() {
        for (c in courses) {
            db.child("courses").child(c.shortName)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(s: DataSnapshot) {
                        c.longName = s.child("name").value.toString()
                        c.shortDescription = s.child("description_short").value.toString()
                        c.longDescription = s.child("description_long").value.toString()
                        c.avgCourseScore = s.child("avg_course_score").value.toString().toDouble()

                        getTeacher("lecturer", c.courseLecturerId, c)
                        getTeacher("examiner", c.courseExaminerId, c)

                        val teachers = mutableListOf<Int>()
                        s.child("teachers").children.forEach {
                            teachers.add(it.value.toString().toInt())
                        }

                        adapter.addItem(c)
                        checkIfAdapterEmpty()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(error.message, "myCoursesFragment loadCourses event fail")
                    }
                })
        }
    }

    private fun getTeacher(type: String, teacherId: Int, course: Course) {
        db.child("teachers").child(teacherId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (type == "examiner") {
                        course.courseExaminer = snapshot.child("name").value.toString()
                        course.avgExaminerScore = snapshot.child("avg_examiner_score").value.toString().toDouble()
                        adapter.replaceItem(course)
                        checkIfAdapterEmpty()
                    }
                    else if (type == "lecturer") {
                        course.courseLecturer = snapshot.child("name").value.toString()
                        course.avgLecturerScore = snapshot.child("avg_lecturer_score").value.toString().toDouble()
                        adapter.replaceItem(course)
                        checkIfAdapterEmpty()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "MyCourseFragment db error while reading teachers")
                }
            })
    }

    fun checkIfAdapterEmpty() {
        if (adapter.isEmpty()) {
            view?.findViewById<TextView>(R.id.tvMyCoursesEmpty)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.btnMyCoursesGotoSearch)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.tvMyCoursesEmpty)?.visibility = View.INVISIBLE
            view?.findViewById<Button>(R.id.btnMyCoursesGotoSearch)?.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(course: Course) {
        val fm = parentFragmentManager
        val fragment = CourseFragment(course, adapter)
        fragment.show(fm, "Course Fragment")
    }
}