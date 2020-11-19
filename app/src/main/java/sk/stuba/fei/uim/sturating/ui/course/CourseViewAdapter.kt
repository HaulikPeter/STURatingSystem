package sk.stuba.fei.uim.sturating.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_course.view.*
import sk.stuba.fei.uim.sturating.R

class CourseViewAdapter : RecyclerView.Adapter<CourseViewAdapter.ViewHolder>() {

    private val courseList = mutableListOf<Course>()

    var itemClickLister: CourseItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courseList[position]

        holder.course = course
        holder.tvSmallCourseName.text = (course.shortName + course.longName)
        holder.tvSmallCourseLecturer.text = course.courseLecturer
        holder.tvSmallCourseAvgScr.text = course.avgCourseScore.toString()
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