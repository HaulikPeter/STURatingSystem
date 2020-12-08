package sk.stuba.fei.uim.sturating.ui.course

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import sk.stuba.fei.uim.sturating.R

class CourseFragment(
    /*TODO: given element db reference*/
    private val course: Course
) : DialogFragment() {

    private var tvCourse: TextView? = null
    private var tvCourseDesc: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_course_fragment, container, false)
        dialog?.setTitle("Course Fragment")

        tvCourse = root.findViewById(R.id.tvCourse)
        tvCourse?.text = course.shortName
        tvCourse?.tag = "short"
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
        for (i in 1..course.avgCourseScore)
            asc.append("⭐")
        val asl = root.findViewById<TextView>(R.id.tvAvgLecturerScore)
        asl.text = ""
        for (i in 1..course.avgLecturerScore)
            asl.append("⭐")
        val ase = root.findViewById<TextView>(R.id.tvAvgExaminerScore)
        ase.text = ""
        for (i in 1..course.avgExaminerScore)
            ase.append("⭐")

        // TODO: check db if course was added, by that enable/disable the buttons
        val btnAddRating: Button = root.findViewById(R.id.btnAddRating)
        btnAddRating.isEnabled = false
        btnAddRating.setTextColor(Color.GRAY)

        return root
    }

    private fun onNameClick() {
        tvCourse?.let {
            if (it.tag == "short") {
                //TODO: from db fullName
                //it.text = "Mathematical Statistics"
                it.text = course.longName
                it.tag = "long"
            } else {
                //TODO: from db shortName
                //it.text = "B-MTSTA"
                it.text = course.shortName
                it.tag = "short"
            }
        }
    }

    private fun onDescClick() {
        //TODO: from db fullDesc
        val shortDesc = course.shortDescription +
                "<br /><font color='blue'>More Info</font>"
        //TODO: from db shortDesc
        val longDesc = course.longDescription +
                "<br /><font color='blue'>Less Info</font>"

        if ("short" == tvCourseDesc?.tag) {
            tvCourseDesc?.setText(Html.fromHtml(longDesc, HtmlCompat.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
            tvCourseDesc?.tag = "long"
        } else {
            tvCourseDesc?.setText(Html.fromHtml(shortDesc, HtmlCompat.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
            tvCourseDesc?.tag = "short"
        }
    }
}