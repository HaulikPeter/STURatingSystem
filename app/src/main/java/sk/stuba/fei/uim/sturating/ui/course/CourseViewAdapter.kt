package sk.stuba.fei.uim.sturating.ui.course

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_course.view.*
import sk.stuba.fei.uim.sturating.R
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import kotlin.math.floor

class CourseViewAdapter : RecyclerView.Adapter<CourseViewAdapter.ViewHolder>() {

    private var courseList = mutableListOf<Course>()

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
        if (course.courseLecturer == "-1") {
            holder.tvSmallCourseLecturer.visibility = View.INVISIBLE
            holder.tvSmallCourseLecturerLabel.visibility = View.INVISIBLE
        }
        else {
            holder.tvSmallCourseLecturer.visibility = View.VISIBLE
            holder.tvSmallCourseLecturer.text = course.courseLecturer
            holder.tvSmallCourseLecturerLabel.visibility = View.VISIBLE
        }
        holder.tvSmallCourseAvgScr.text = ""
        for (i in 1..(floor(course.avgCourseScore).toInt())) {
            holder.tvSmallCourseAvgScr.append("⭐")
        }
    }

    override fun getItemCount() = courseList.size

    fun isEmpty() = courseList.isEmpty()

    fun hasItem(id: Int): Boolean {
        return try {
            courseList[id]
            true
        } catch (e: IndexOutOfBoundsException) {
            Log.w("Element not exists", e.toString())
            false
        }
    }

    fun hasItem(courseName: String): Boolean {
        courseList.forEach{
            if (it.shortName === courseName)
                return true
        }
        return false
    }

    fun addItem(course: Course) {
        if (!courseList.contains(course)) {
            courseList.add(course)
            notifyDataSetChanged()
        }
    }

    fun addAllItem(mutableList: MutableList<Course>) {
        courseList.clear()
        courseList = mutableList
        notifyDataSetChanged()
    }

    fun replaceItem(course: Course) {
        if (courseList.remove(course)) {
            courseList.add(course)
            notifyDataSetChanged()
        }
    }

    fun removeItem(course: Course) = courseList.remove(course)

    fun removeItem(id: Int) = courseList.removeAt(id)

    fun removeAll() {
        courseList = mutableListOf()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSmallCourseName: TextView = itemView.tvSmallCourseName
        val tvSmallCourseLecturer: TextView = itemView.tvSmallCourseLecturer
        val tvSmallCourseAvgScr: TextView = itemView.tvSmallCourseAvgScr

        val tvSmallCourseLecturerLabel: TextView = itemView.tvSmallCourseLecturerLabel

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