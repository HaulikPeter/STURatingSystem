package sk.stuba.fei.uim.sturating.ui.course

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_course.view.*
import sk.stuba.fei.uim.sturating.R

class CourseViewAdapter(context: Context) : RecyclerView.Adapter<CourseViewAdapter.ViewHolder>() {

    //private val courseList = mutableListOf<Course>()
    private val courseList: MutableList<Course> = mutableListOf(
        Course("B-AAAA", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
        "AAAAAAAA", "LONG AAAAAAA",
        3, 1, 1),
        Course("B-BBBB", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            "BBBBBBBB", "LONG BBBBBBBBB",
            2, 1, 5),
        Course("B-CCCC", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            1, 1, 4),
        Course("B-DDDD", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            2, 1, 1),
        Course("B-EEEE", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            3, 1, 1),
        Course("B-FFFF", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            5, 1, 1),
        Course("B-GGGG", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            5, 1, 1),
        Course("B-HHHH", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            4, 1, 1),
        Course("B-IIII", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            1, 1, 1),
        Course("B-JJJJ", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            1, 1, 1),
        Course("B-KKKK", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            2, 1, 1),
        Course("B-LLLL", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            5, 4, 1),
        Course("B-MMMM", "Mathematical Statistics",
            "doc. RNDr. Oľga Nánásiová, PhD.", "Mgr. Dávid Pancza, PhD.",
            context.getString(R.string.course_description), "LONG" + context.getString(R.string.course_description),
            3, 5, 1)
    )

    var itemClickLister: CourseItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courseList[position]

        holder.course = course
        holder.tvSmallCourseName.text = (course.shortName + " " + course.longName)
        holder.tvSmallCourseLecturer.text = course.courseLecturer
        holder.tvSmallCourseAvgScr.text = ""
        for (i in 1..course.avgCourseScore) {
            holder.tvSmallCourseAvgScr.append("⭐")
        }
    }

    override fun getItemCount() = courseList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSmallCourseName: TextView = itemView.tvSmallCourseName
        val tvSmallCourseLecturer: TextView = itemView.tvSmallCourseLecturer
        val tvSmallCourseAvgScr: TextView = itemView.tvSmallCourseAvgScr

        var course: Course? = null

        init {
            itemView.setOnClickListener {
                course?.let { itemClickLister?.onItemClick(it) }
            }
        }
    }

    interface CourseItemClickListener {
        fun onItemClick(course: Course)
    }
}