package sk.stuba.fei.uim.sturating.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.Course
import sk.stuba.fei.uim.sturating.ui.course.CourseViewAdapter
import java.lang.Exception
import java.util.*

class SearchResultFragment(private val parent: SearchFragment,
                           private val searchText: String) :
    Fragment(), CourseViewAdapter.CourseItemClickListener {

    private lateinit var courseViewAdapter: CourseViewAdapter
    private lateinit var rvCourses: RecyclerView

    private lateinit var tvMyCoursesEmpty: TextView
    private lateinit var btnSearchCourse: Button
    
    private val db = FirebaseDatabase.getInstance().reference

    private val courseNames = mutableListOf<String>()

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

        tvMyCoursesEmpty = root.findViewById(R.id.tvMyCoursesEmpty)
        btnSearchCourse = root.findViewById(R.id.btnMyCoursesGotoSearch)

        btnSearchCourse.apply {
            isEnabled = false
            visibility = View.INVISIBLE
        }
        "No such course for that search".also { tvMyCoursesEmpty.text = it }
        tvMyCoursesEmpty.visibility = View.VISIBLE

        return root
    }

    private fun readCourses() {
        val query = db.child("courses")
            .orderByKey()
            .startAt(searchText.uppercase(Locale.ROOT))
            .endAt(searchText.uppercase(Locale.ROOT) + "\\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                courseNames.clear()
                snapshot.children.forEach {
                    courseNames.add(it.key.toString())
                }
                loadCourses()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelled", "search interface")
            }
        })
    }

    private fun loadCourses() {
        courseNames.forEach { courseName ->
            try {
                db.child("courses").child(courseName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val course = Course(
                                id = -1,
                                shortName = courseName,
                                longName = snapshot.child("name").value.toString(),
                                shortDescription = snapshot.child("description_short").value.toString(),
                                longDescription = snapshot.child("description_long").value.toString(),
                                avgCourseScore = snapshot.child("avg_course_score").value.toString().toDouble())
                                snapshot.child("teachers").children.forEach {
                                    course.teacherIds.add(it.value.toString().toInt())
                                }
                            courseViewAdapter.addItem(course)

                            btnSearchCourse.apply {
                                isEnabled = false
                                visibility = View.INVISIBLE
                            }
                            tvMyCoursesEmpty.visibility = View.INVISIBLE
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d(error.message, "loadCourses foreach")
                        }
                    })

            } catch (e: Exception) {
                Log.w(e.message, "loadCourses error")
            }
        }
    }

    override fun onDetach() {
        parent.etSearchText?.apply {
            visibility = View.VISIBLE
            isEnabled = true
            text = ""
        }
        parent.btnSearch?.apply {
            visibility = TextView.VISIBLE
            isEnabled = true
        }
        super.onDetach()
    }

    override fun onItemClick(course: Course) {
        val fm = parentFragmentManager
        val fragment = SearchCourseDialogFragment(course)
        fragment.show(fm, "Course Detailed Dialog Fragment")
    }
}