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

class CourseFragment : DialogFragment(
    /*TODO: given element db reference*/
) {

    private var tvCourseName: TextView? = null
    private var tvCourseDesc: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_course_fragment, container, false)
        dialog?.setTitle("Course Fragment")

        // TODO: initialize values to courseFragment from database
        tvCourseName = root.findViewById(R.id.tvCourseName)
        tvCourseName?.tag = "short"
        tvCourseName?.setOnClickListener { onNameClick() }

        tvCourseDesc = root.findViewById(R.id.tvCourseDesc)
        tvCourseDesc?.tag = "short"
        tvCourseDesc?.setOnClickListener { onDescClick() }

        val btn: Button = root.findViewById(R.id.btnAddRating)
        btn.isEnabled = false
        btn.setTextColor(Color.GRAY)

        return root
    }

    private fun onNameClick() {
        tvCourseName?.let {
            if (it.tag == "short") {
                //TODO: from db fullName
                it.text = "Mathematical Statistics"
                it.tag = "long"
            } else {
                //TODO: from db shortName
                it.text = "B-MTSTA"
                it.tag = "short"
            }
        }
    }

    private fun onDescClick() {
        //TODO: from db fullDesc
        val shortDesc = getString(R.string.course_description) +
                "<br /><font color='blue'>More Info</font>"
        //TODO: from db shortDesc
        val longDesc = "LONGASDASDASD" +
                getString(R.string.course_description) +
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