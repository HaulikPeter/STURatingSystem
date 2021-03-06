package sk.stuba.fei.uim.sturating.ui.course

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.mycourses.MyCoursesFragment
import kotlin.math.floor

class CourseFragment(
    private val parent: MyCoursesFragment,
    private val course: Course,
    private val adapter: CourseViewAdapter
) : DialogFragment() {

    private var tvCourse: TextView? = null
    private var tvCourseDesc: TextView? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_course_fragment, container, false)
        dialog?.setTitle("Course Fragment")

        tvCourse = root.findViewById(R.id.tvCourse)
        tvCourse?.text = course.longName
        tvCourse?.tag = "long"
        tvCourse?.setOnClickListener { onNameClick() }

        root.findViewById<TextView>(R.id.tvLecturer).text = course.courseLecturer
        root.findViewById<TextView>(R.id.tvExaminer).text = course.courseExaminer

        tvCourseDesc = root.findViewById(R.id.tvCourseDesc)
        tvCourseDesc?.setText(Html.fromHtml(course.shortDescription +
                "<br /><font color='blue'>More Info</font>"), TextView.BufferType.SPANNABLE)
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

        // TODO: check from ADAPTER or check from db???
        root.findViewById<Button>(R.id.btnAddRating).setOnClickListener {
            Toast.makeText(context, "addRating", Toast.LENGTH_SHORT).show()
        }
        checkButtons(root)

        return root
    }

    private val addListener = View.OnClickListener {
        val ref = db.child("users").child(auth.uid.toString())
            .child("courses").child(course.id.toString())
        ref.child("name").setValue(course.shortName)
        ref.child("lecturer_id").setValue(course.courseLecturerId)
        ref.child("examiner_id").setValue(course.courseExaminerId)

        adapter.addItem(course)
        adapter.notifyDataSetChanged()
        checkButtons()
    }
    private val removeListener = View.OnClickListener {
        db.child("users").child(auth.uid.toString())
            .child("courses").child(course.id.toString()).removeValue()
        adapter.removeItem(course)
        adapter.notifyDataSetChanged()
        checkButtons()
        dismiss()
    }

    private fun checkButtons(root: View) {
        val btnAddRemoveCourse = root.findViewById<Button>(R.id.btnAddRemoveCourse)
        val btnAddRating = root.findViewById<Button>(R.id.btnAddRating)

        if (adapter.hasItem(course.shortName)) {
            btnAddRemoveCourse?.text = getString(R.string.btn_remove_course_label)
            btnAddRemoveCourse.setOnClickListener(removeListener)
            btnAddRating?.setTextColor(Color.BLACK)
            btnAddRating?.isEnabled = true
        } else {
            btnAddRemoveCourse?.text = getString(R.string.btn_add_course_label)
            btnAddRemoveCourse.setOnClickListener(addListener)
            btnAddRating?.setTextColor(Color.GRAY)
            btnAddRating?.isEnabled = false
        }
    }

    private fun checkButtons() {
        val btnAddRemoveCourse = requireView().findViewById<Button>(R.id.btnAddRemoveCourse)
        val btnAddRating = requireView().findViewById<Button>(R.id.btnAddRating)

        if (adapter.hasItem(course.shortName)) {
            btnAddRemoveCourse.text = getString(R.string.btn_remove_course_label)
            btnAddRemoveCourse.setOnClickListener(removeListener)
            btnAddRating.setTextColor(Color.BLACK)
            btnAddRating.isEnabled = true
        } else {
            btnAddRemoveCourse.text = getString(R.string.btn_add_course_label)
            btnAddRemoveCourse.setOnClickListener(addListener)
            btnAddRating.setTextColor(Color.GRAY)
            btnAddRating.isEnabled = false
        }
        parent.checkIfAdapterEmpty()
    }

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