package sk.stuba.fei.uim.sturating.ui.toplist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.Course
import sk.stuba.fei.uim.sturating.ui.course.CourseViewAdapter
import sk.stuba.fei.uim.sturating.ui.search.SearchCourseDialogFragment

// class for the toplist fragment
class TopListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val db = FirebaseDatabase.getInstance().reference

    private lateinit var rvTopList: RecyclerView
    private lateinit var adapter: CourseViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_top_list, container, false)

        // spinner is used to select which of the three should be listed out
        val spinner: Spinner = root.findViewById(R.id.spinnerTopList)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.top_list_spinner_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        rvTopList = root.findViewById(R.id.rvTopList)
        adapter = CourseViewAdapter()

        rvTopList.adapter = adapter

        return root
    }

    // depending on the selection the following function will be initiated
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val list = resources.getStringArray(R.array.top_list_spinner_array)
        when(parent?.getItemAtPosition(position)) {
            list[0] -> onSelectCourses()
            list[1] -> onSelectLecturers()
            list[2] -> onSelectExaminers()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun onSelectCourses() {
        adapter = CourseViewAdapter()
        // on course selection the user can open the course itself which is done here
        adapter.itemClickLister = object : CourseViewAdapter.CourseItemClickListener {
            override fun onItemClick(course: Course) {
                val fm = parentFragmentManager
                val fragment = SearchCourseDialogFragment(course)
                fragment.show(fm, "Course Detailed Dialog Fragment")
            }
        }
        // then the courses are fetched from the database, ordered and listed
        view?.findViewById<RecyclerView>(R.id.rvTopList)?.adapter = adapter
        db.child("courses")
            .orderByChild("avg_course_score").limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.toMutableList()
                for (i in list.size-1 downTo 0) {
                    val course = Course(
                        id = i,
                        shortName = list[i].key.toString(),
                        shortDescription = list[i].child("description_short").value.toString(),
                        longName = list[i].child("name").value.toString(),
                        longDescription = list[i].child("description_long").value.toString(),
                        avgCourseScore = list[i].child("avg_course_score").value.toString().toDouble()
                    )
                    list[i].child("teachers").children.forEach {
                        course.teacherIds.add(it.value.toString().toInt())
                    }
                    adapter.addItem(course)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(error.message, "top list avg score read fail")
            }
        })
    }

    // same applies to the lecturers as well as for the course
    private fun onSelectLecturers() {
        adapter = CourseViewAdapter(CourseViewAdapter.TYPE_TOP_LECTURERS)
        view?.findViewById<RecyclerView>(R.id.rvTopList)?.adapter = adapter
        db.child("teachers")
            .orderByChild("avg_lecturer_score")
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.toList()
                    for (i in list.size-1 downTo 0) {
                        adapter.addItem(Course(
                            id = list[i].key.toString().toInt(),
                            courseLecturer = list[i].child("name").value.toString(),
                            avgLecturerScore = list[i].child("avg_lecturer_score").value.toString().toDouble(),
                            avgExaminerScore = list[i].child("avg_examiner_score").value.toString().toDouble()
                        ))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "top list avg lecturer score read fail")
                }
            })
    }

    // same applies to the examiner as well as for the course
    private fun onSelectExaminers() {
        adapter = CourseViewAdapter(CourseViewAdapter.TYPE_TOP_EXAMINERS)
        view?.findViewById<RecyclerView>(R.id.rvTopList)?.adapter = adapter
        db.child("teachers")
            .orderByChild("avg_examiner_score")
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.toList()
                    for (i in list.size-1 downTo 0)
                        adapter.addItem(Course(
                            id = list[i].key.toString().toInt(),
                            courseExaminer = list[i].child("name").value.toString(),
                            avgLecturerScore = list[i].child("avg_lecturer_score").value.toString().toDouble(),
                            avgExaminerScore = list[i].child("avg_examiner_score").value.toString().toDouble()
                        ))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "top list avg examiner score read fail")
                }
            })
    }
}