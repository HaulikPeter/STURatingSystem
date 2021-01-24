package sk.stuba.fei.uim.sturating.ui.mycourses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.*

class MyCoursesFragment : Fragment(), CourseViewAdapter.CourseItemClickListener {

    private lateinit var courseViewAdapter: CourseViewAdapter
    private lateinit var rvCourses: RecyclerView
    private var coursesExist: Boolean? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_courses_filled, container, false)
        rvCourses = root.findViewById(R.id.rvCourses)
        courseViewAdapter = CourseViewAdapter()
        courseViewAdapter.itemClickLister = this
        readCourses()
        rvCourses.adapter = courseViewAdapter


        // TODO: READ FROM DB IF THERE IS ANY COURSE...
//        if (coursesExist!!) {
//            root = inflater.inflate(R.layout.fragment_my_courses_filled, container, false)
//            rvCourses = root.findViewById(R.id.rvCourses)
//            courseViewAdapter = CourseViewAdapter(requireContext())
//            courseViewAdapter.itemClickLister = this@MyCoursesFragment
//            rvCourses.adapter = courseViewAdapter
//        } else {
//            root = inflater.inflate(R.layout.fragment_my_courses_empty, container, false)
//        }

        //----------------------------

        //TODO: if list is empty, inflate empty
//        var root: View? = null
//        val auth = FirebaseAuth.getInstance()
//        val db = FirebaseDatabase.getInstance().reference
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val asd = "asd"
//                    //todo be kellene csukni az elozoket a stackon
//                    root = inflater.inflate(R.layout.fragment_my_courses_empty, container, false)
//                    root!!.findViewById<Button>(R.id.btnFragment).setOnClickListener { }//click() }
//                }
//                else {
//                    val dsa = "dsa"
//                    //todo be kellene csukni az elozoket a stackon
//                    root = inflater.inflate(R.layout.fragment_my_courses_filled, container, false)
//                    rvCourses = root!!.findViewById(R.id.rvCourses)
//                    courseViewAdapter = CourseViewAdapter(requireContext())
//                    courseViewAdapter.itemClickLister = this@MyCoursesFragment
//                    rvCourses.adapter = courseViewAdapter
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w("Failed to read courses", error.toException())
//            }
//        }
//        db.child("users").child(auth.uid.toString()).child("courses")
//            .addValueEventListener(listener)

//        val root = inflater.inflate(R.layout.fragment_my_courses_empty, container, false)
//        root.findViewById<Button>(R.id.btnFragment).setOnClickListener { }//click() }

        //TODO: else inflate filled fragment
//        val root = inflater.inflate(R.layout.fragment_my_courses_filled, container, false)
//        rvCourses = root.findViewById(R.id.rvCourses)
//        courseViewAdapter = CourseViewAdapter(requireContext())
//        courseViewAdapter.itemClickLister = this
//
//        rvCourses.adapter = courseViewAdapter

        return root
    }

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    private val courseNames = mutableListOf<String>()
    private fun readCourses() {
        readData(
            db.child("users").child(auth.uid.toString()).child("courses"),
            object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    courseNames.clear()
                    for (child in dataSnapshot.children)
                        courseNames.add(child.value.toString())
                    loadCourses()
                }

                override fun onStart() {
                    Log.w("onStart()", "Started")
                }

                override fun onFailure(error: DatabaseError) {
                    Log.w("onFailure failed", error.toException())
                }
            })
    }

    private fun loadCourses() {
        for (courseName in courseNames) {
            readData(
                db.child("courses").child(courseName),
                object : OnGetDataListener {
                    override fun onSuccess(dataSnapshot: DataSnapshot) {
                        val course = Course(
                            shortName = courseName,
                            longName = dataSnapshot.child("name").value.toString(),
                            shortDescription = dataSnapshot.child("description_short").value.toString(),
                            longDescription = dataSnapshot.child("description_long").value.toString(),
                            avgCourseScore = dataSnapshot.child("avg_course_score").value.toString().toDouble()
                        )

                        getTeacher("lecturer", dataSnapshot.child("lecturer_id").value.toString(), course)
                        getTeacher("examiner", dataSnapshot.child("examiner_id").value.toString(), course)

                        courseViewAdapter.addItem(course)
                    }

                    override fun onStart() { Log.w("loadCourses()", "started") }

                    override fun onFailure(error: DatabaseError) { Log.w("loadCourses() failed", error.toException()) }
                }
            )
        }
    }

    private fun getTeacher(type: String, teacherId: String, course: Course) {
        readData(
            db.child("teachers").child(teacherId),
            object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    if (type == "examiner") {
                        course.courseExaminer = dataSnapshot.child("name").value.toString()
                        course.avgExaminerScore = dataSnapshot.child("avg_examiner_score").value.toString().toDouble()
                        courseViewAdapter.replaceItem(course)
                    }
                    else if (type == "lecturer") {
                        course.courseLecturer = dataSnapshot.child("name").value.toString()
                        course.avgLecturerScore = dataSnapshot.child("avg_lecturer_score").value.toString().toDouble()
                        courseViewAdapter.replaceItem(course)
                    }
                }

                override fun onStart() { Log.w("getTeacher.onStart()", "started") }

                override fun onFailure(error: DatabaseError) { Log.w("getTeacher.onFailure()", error.toException()) }
            }
        )
    }

    private fun readData(ref: DatabaseReference, listener: OnGetDataListener) {
        listener.onStart()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listener.onSuccess(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onFailure(error)
            }
        })
    }

    override fun onItemClick(course: Course) {
        //TODO: open course as dialog fragment
        val fm = parentFragmentManager
        val fragment = CourseFragment(course)
        fragment.show(fm, "Course Fragment")
    }
}