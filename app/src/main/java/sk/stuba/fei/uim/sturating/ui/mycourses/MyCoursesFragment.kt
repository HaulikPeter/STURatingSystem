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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_item_search_course_teachers.*
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.*

class MyCoursesFragment : Fragment(), CourseViewAdapter.CourseItemClickListener {

    private lateinit var courseViewAdapter: CourseViewAdapter
    private lateinit var rvCourses: RecyclerView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private val courseNames = mutableMapOf<Int, String>()
    //private val courseNames = mutableListOf<String>()

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
        return root
    }

//    private fun readCourses() {
//        readData(
//            db.child("users").child(auth.uid.toString()).child("courses"),
//            object : OnGetDataListener {
//                override fun onSuccess(dataSnapshot: DataSnapshot) {
//                    courseNames.clear()
//                    for (child in dataSnapshot.children)
//                        //courseNames[child.key?.toInt() ?: -1] = child.key ?: ""
//                                                                                                                //courseNames[child.key?.toInt() ?: -1] = child.value.toString()
//                        courseNames[child.key?.toInt() ?: -1] = child.child("name").value.toString()
//                    loadCourses(dataSnapshot)
//                }
//
//                override fun onStart() {
//                    Log.w("onStart()", "Started")
//                }
//
//                override fun onFailure(error: DatabaseError) {
//                    Log.w("onFailure failed", error.toException())
//                }
//            })
//    }

    private val courses = mutableListOf<Course>()

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
                .addListenerForSingleValueEvent(object : ValueEventListener {
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

                        courseViewAdapter.addItem(c)
                        checkIfAdapterEmpty()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(error.message, "myCoursesFragment loadCourses event fail")
                    }
                })
        }
    }


//    private fun loadCourses(userSnapshot: DataSnapshot) {
//        for (courseName in courseNames) {
//            readData(
//                db.child("courses").child(courseName.value),
//                object : OnGetDataListener {
//                    override fun onSuccess(dataSnapshot: DataSnapshot) {
//                        val course = Course(
//                            id = courseName.key,
//                            shortName = courseName.value,
//                            longName = dataSnapshot.child("name").value.toString(),
//                            shortDescription = dataSnapshot.child("description_short").value.toString(),
//                            longDescription = dataSnapshot.child("description_long").value.toString(),
//                            avgCourseScore = dataSnapshot.child("avg_course_score").value.toString().toDouble(),
//                        )
//
//                        //TODO TODO
//                        getTeacher("lecturer", userSnapshot.child(courseName.key.toString()).child("lecturer_id").value.toString(), course)
//                        getTeacher("examiner", userSnapshot.child(courseName.key.toString()).child("examiner_id").value.toString(), course)
//                        //getTeacher("lecturer", dataSnapshot.child("lecturer_id").value.toString(), course)
//                        //getTeacher("examiner", dataSnapshot.child("examiner_id").value.toString(), course)
//
//                        courseViewAdapter.addItem(course)
//                        checkIfAdapterEmpty()
//                    }
//
//                    override fun onStart() { Log.w("loadCourses()", "started") }
//
//                    override fun onFailure(error: DatabaseError) { Log.w("loadCourses() failed", error.toException()) }
//                }
//            )
//        }
//    }

    private fun getTeacher(type: String, teacherId: Int, course: Course) {
        db.child("teachers").child(teacherId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (type == "examiner") {
                        course.courseExaminer = snapshot.child("name").value.toString()
                        course.avgExaminerScore = snapshot.child("avg_examiner_score").value.toString().toDouble()
                        courseViewAdapter.replaceItem(course)
                        checkIfAdapterEmpty()
                    }
                    else if (type == "lecturer") {
                        course.courseLecturer = snapshot.child("name").value.toString()
                        course.avgLecturerScore = snapshot.child("avg_lecturer_score").value.toString().toDouble()
                        courseViewAdapter.replaceItem(course)
                        checkIfAdapterEmpty()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "MyCourseFragment db error while reading teachers")
                }
            })
    }

//    private fun getTeacher(type: String, teacherId: String, course: Course) {
//        readData(
//            db.child("teachers").child(teacherId),
//            object : OnGetDataListener {
//                override fun onSuccess(dataSnapshot: DataSnapshot) {
//                    if (type == "examiner") {
//                        course.courseExaminerId = teacherId.toInt()
//                        course.courseExaminer = dataSnapshot.child("name").value.toString()
//                        course.avgExaminerScore = dataSnapshot.child("avg_examiner_score").value.toString().toDouble()
//                        courseViewAdapter.replaceItem(course)
//                        checkIfAdapterEmpty()
//                    }
//                    else if (type == "lecturer") {
//                        course.courseLecturerId = teacherId.toInt()
//                        course.courseLecturer = dataSnapshot.child("name").value.toString()
//                        course.avgLecturerScore = dataSnapshot.child("avg_lecturer_score").value.toString().toDouble()
//                        courseViewAdapter.replaceItem(course)
//                        checkIfAdapterEmpty()
//                    }
//                }
//
//                override fun onStart() { Log.w("getTeacher.onStart()", "started") }
//
//                override fun onFailure(error: DatabaseError) { Log.w("getTeacher.onFailure()", error.toException()) }
//            }
//        )
//    }

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

    fun checkIfAdapterEmpty() {
        if (courseViewAdapter.isEmpty()) {
            view?.findViewById<TextView>(R.id.tvMyCoursesEmpty)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.btnSearchCourse)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.tvMyCoursesEmpty)?.visibility = View.INVISIBLE
            view?.findViewById<Button>(R.id.btnSearchCourse)?.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(course: Course) {
        val fm = parentFragmentManager
        val fragment = CourseFragment(course, courseViewAdapter)
        fragment.show(fm, "Course Fragment")
    }
}