package sk.stuba.fei.uim.sturating.ui.search

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.Course

import sk.stuba.fei.uim.sturating.ui.course.Teacher
import java.util.ArrayList
import kotlin.math.floor

class SearchCourseDialogFragment(
    private val course: Course
) : DialogFragment() {

    private var tvSearchCourseName: TextView? = null
    private var tvSearchCourseDesc: TextView? = null

    private var btnAddRemoveCourseSearch: Button? = null

    private lateinit var scdAdapter: SearchCourseDialogTeachersViewAdapter
    private lateinit var rvTeachers: RecyclerView

    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search_course, container, false)
        dialog?.setTitle(tag)

        tvSearchCourseName = root.findViewById(R.id.tvSearchCourseName)
        tvSearchCourseName?.text = course.longName
        tvSearchCourseName?.tag = "long"
        tvSearchCourseName?.setOnClickListener{ onNameClick() }

        tvSearchCourseDesc = root.findViewById(R.id.tvSearchCourseDesc)
        tvSearchCourseDesc?.setText(Html.fromHtml(course.shortDescription +
                "<br /><font color='blue'>More Info</font>", Html.FROM_HTML_MODE_LEGACY),
            TextView.BufferType.SPANNABLE)
        tvSearchCourseDesc?.tag = "short"
        tvSearchCourseDesc?.setOnClickListener { onDescClick() }

        val asc = root.findViewById<TextView>(R.id.tvSearchAvgCourseScore)
        asc.text = ""
        for (i in 1..(floor(course.avgCourseScore)).toInt())
            asc.append("⭐")

        scdAdapter = SearchCourseDialogTeachersViewAdapter()
        rvTeachers = root.findViewById(R.id.rvSearchCourseTeachers)
        scdAdapter.clear()
        loadTeachers(course.teacherIds)
        rvTeachers.adapter = scdAdapter

        // button on search result dialog frame to add/remove course
        btnAddRemoveCourseSearch = root.findViewById(R.id.btnAddRemoveCourseSearch)
        btnAddRemoveCourseSearch?.tag = false
        db.child("users").child(auth.uid.toString()).child("courses")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.child("name").value.toString() == course.shortName) {
                            btnAddRemoveCourseSearch?.tag = true
                            checkButton()
                            return
                        }
                    }
                    btnAddRemoveCourseSearch?.tag = false
                    checkButton()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "unable to read courses list from db/users")
                }
            })

        btnAddRemoveCourseSearch?.setOnClickListener {
            btnAddRemoveCourseSearch?.tag = !(btnAddRemoveCourseSearch?.tag as Boolean)
            checkButton()
        }

        return root
    }

    private val addListener = View.OnClickListener {
        db.child("courses").child(course.shortName).child("teachers")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val teacherIds = mutableListOf<Int>()
                    snapshot.children.forEach {
                        teacherIds.add(it.value.toString().toInt())
                    }

                    val fm = parentFragmentManager
                    val fragment = SearchAddCourseTeachers(teacherIds, course)
                    fragment.show(fm, "Search Add Course Teachers Fragment")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "add failed in search course fragment")
                }
            })
    }

    private val removeListener = View.OnClickListener {
        db.child("users").child(auth.uid.toString()).child("courses")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (course.shortName == it.child("name").value.toString()) {
                            db.child("users").child(auth.uid.toString())
                                .child("courses")
                                .child(it.key.toString()).removeValue()
                            checkButton()
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "remove failed in search course fragment")
                }
            })
    }

    private fun checkButton() {
        if (btnAddRemoveCourseSearch?.tag as Boolean) {
            btnAddRemoveCourseSearch?.apply {
                text = getString(R.string.btn_remove_course_label)
                setOnClickListener(removeListener)
                tag = false
            }
        } else {
            btnAddRemoveCourseSearch?.apply {
                text = getString(R.string.btn_add_course_label)
                setOnClickListener(addListener)
                tag = true
            }
        }
    }

    private fun loadTeachers(teacherIds: ArrayList<Int>) {
        teacherIds.forEach { id ->
            db.child("teachers").child(id.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val teacher = Teacher(
                            name = snapshot.child("name").value.toString(),
                            avgLecturerScore = snapshot.child("avg_lecturer_score").value
                                .toString().toDouble(),
                            avgExaminerScore = snapshot.child("avg_examiner_score").value
                                .toString().toDouble(),
                            examinerRatingCount = snapshot.child("examiner_rating_count").value
                                .toString().toDouble().toInt(),
                            lecturerRatingCount = snapshot.child("lecturer_rating_count").value
                                .toString().toDouble().toInt()
                        )

                        scdAdapter.addItem(teacher)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(error.message, "course search result dialog teachers list error")
                    }
                })
        }
    }

    private fun onNameClick() {
        tvSearchCourseName?.let {
            if (it.tag == "short") {
                it.text = course.longName
                it.tag = "long"
            } else {
                it.text = course.shortName
                it.tag = "short"
            }
        }
    }

    private fun onDescClick() {
        val shortDesc = course.shortDescription +
                "<br /><font color='blue'>More Info</font>"
        val longDesc = course.longDescription +
                "<br /><font color='blue'>Less Info</font>"

        if ("short" == tvSearchCourseDesc?.tag) {
            tvSearchCourseDesc?.setText(
                Html.fromHtml(longDesc, HtmlCompat.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE)
            tvSearchCourseDesc?.tag = "long"
        } else {
            tvSearchCourseDesc?.setText(
                Html.fromHtml(shortDesc, HtmlCompat.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE)
            tvSearchCourseDesc?.tag = "short"
        }
    }
}