package sk.stuba.fei.uim.sturating.ui.course

import android.graphics.Color
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.mycourses.AddRatingDialogFragment
import kotlin.math.floor

// course fragment that displays a single course
class CourseFragment(
    private val course: Course,
    private val adapter: CourseViewAdapter
) : DialogFragment() {

    private var tvCourse: TextView? = null
    private var tvCourseDesc: TextView? = null

    private var btnAddRemoveCourse: Button? = null
    private var btnAddRating: Button? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // it sets up the view with the data provided from the constructor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mycourses_course, container, false)
        dialog?.setTitle("Course Fragment")

        tvCourse = root.findViewById(R.id.tvCourse)
        tvCourse?.text = course.longName
        tvCourse?.tag = "long"
        tvCourse?.setOnClickListener { onNameClick() }

        tvCourseDesc = root.findViewById(R.id.tvCourseDesc)
        tvCourseDesc?.setText(Html.fromHtml(course.shortDescription +
                "<br /><font color='blue'>More Info</font>", Html.FROM_HTML_MODE_LEGACY),
            TextView.BufferType.SPANNABLE)
        tvCourseDesc?.tag = "short"
        tvCourseDesc?.setOnClickListener { onDescClick() }

        val asc = root.findViewById<TextView>(R.id.tvAvgCourseScore)
        asc.text = ""
        for (i in 1..(floor(course.avgCourseScore)).toInt())
            asc.append("⭐")
        val asl = root.findViewById<TextView>(R.id.tvAvgLecturerScore)
        asl.text = ""
        for (i in 1..(floor(course.avgLecturerScore)).toInt())
            asl.append("⭐")
        val ase = root.findViewById<TextView>(R.id.tvAvgExaminerScore)
        ase.text = ""
        for (i in 1..(floor(course.avgExaminerScore).toInt()))
            ase.append("⭐")

        root.findViewById<TextView>(R.id.tvLecturer).text = course.courseLecturer
        root.findViewById<TextView>(R.id.tvExaminer).text = course.courseExaminer

        btnAddRemoveCourse = root.findViewById(R.id.btnAddRemoveCourse)
        btnAddRating = root.findViewById(R.id.btnAddRating)
        btnAddRemoveCourse?.tag = false
        db.child("users").child(auth.uid.toString()).child("courses")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.child("name").value.toString() == course.shortName) {
                            btnAddRemoveCourse?.tag = true
                            checkButtons()
                            return
                        }
                    }
                    btnAddRemoveCourse?.tag = false
                    checkButtons()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "my courses error reading courses from user")
                }
            })
        btnAddRemoveCourse?.setOnClickListener {
            btnAddRemoveCourse?.tag = !(btnAddRemoveCourse?.tag as Boolean)
            checkButtons()
        }

        btnAddRating?.setOnClickListener { onAddRatingClicked() }

        return root
    }

    // when user adds the rating the teachers selector opens
    private fun onAddRatingClicked() {
        val fm = parentFragmentManager
        val fragment = AddRatingDialogFragment(course)
        fragment.show(fm, "Add Rating Dialog Fragment")
    }

    // add listener for the button in case the user does not have it
    private val addListener = View.OnClickListener {
        val ref = db.child("users").child(auth.uid.toString())
            .child("courses").child(course.id.toString())
        ref.child("name").setValue(course.shortName)
        ref.child("lecturer_id").setValue(course.courseLecturerId)
        ref.child("examiner_id").setValue(course.courseExaminerId)

        adapter.addItem(course)
        checkButtons()
    }

    // remove listener for the button in case the user has it
    private val removeListener = View.OnClickListener {
        db.child("users").child(auth.uid.toString()).child("courses")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (course.shortName == it.child("name").value.toString()) {
                            db.child("users").child(auth.uid.toString())
                                .child("courses")
                                .child(it.key.toString()).removeValue()
                            adapter.removeItem(course)
                            checkButtons()
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(error.message, "removeListener on myCourses remove btn")
                }
            })
    }

    // checking the buttons whether the has it
    private fun checkButtons() {
            if (btnAddRemoveCourse?.tag as Boolean) {
                btnAddRemoveCourse?.apply {
                    text = getString(R.string.btn_remove_course_label)
                    setOnClickListener(removeListener)
                    tag = false
                }
                btnAddRating?.apply {
                    isEnabled = true
                    setTextColor(Color.BLACK)
                }
            } else {
                btnAddRemoveCourse?.apply {
                    text = getString(R.string.btn_add_course_label)
                    setOnClickListener(addListener)
                    tag = true
                }
                btnAddRating?.apply {
                    setTextColor(Color.GRAY)
                    isEnabled = false
                }
            }
    }

    // when clicked on the name of the course it switches from course code to full name and vice versa
    private fun onNameClick() {
        tvCourse?.let {
            if (it.tag == "short") {
                it.text = course.longName
                it.tag = "long"
            } else {
                it.text = course.shortName
                it.tag = "short"
            }
        }
    }

    // clicking on the description will expand or shrinks the description
    private fun onDescClick() {
        val shortDesc = course.shortDescription +
                "<br /><font color='blue'>More Info</font>"
        val longDesc = course.longDescription +
                "<br /><font color='blue'>Less Info</font>"

        if ("short" == tvCourseDesc?.tag) {
            tvCourseDesc?.setText(
                Html.fromHtml(longDesc, HtmlCompat.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE)
            tvCourseDesc?.tag = "long"
        } else {
            tvCourseDesc?.setText(
                Html.fromHtml(shortDesc, HtmlCompat.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE)
            tvCourseDesc?.tag = "short"
        }
    }
}