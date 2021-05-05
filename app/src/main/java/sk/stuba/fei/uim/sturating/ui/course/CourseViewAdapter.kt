package sk.stuba.fei.uim.sturating.ui.course

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.sturating.R
import java.lang.IndexOutOfBoundsException
import kotlin.math.floor

class CourseViewAdapter() : RecyclerView.Adapter<CourseViewAdapter.ViewHolder>() {

    companion object {
        const val TYPE_NORMAL = 0
        const val TYPE_TOP_LECTURERS = 1
        const val TYPE_TOP_EXAMINERS = 2
    }

    private var type = TYPE_NORMAL

    constructor(type: Int) : this() {
        if (type == TYPE_TOP_LECTURERS || type == TYPE_TOP_EXAMINERS)
            this.type = type
    }

    var itemClickLister: CourseItemClickListener? = null

    private var courseList = mutableListOf<Course>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courseList[position]
        holder.course = course

        when (type) {
            TYPE_NORMAL -> {
                holder.tvSmallCourseName.text = (course.shortName + " " + course.longName)
                if (course.courseLecturer == "-1") {
                    holder.tvSmallCourseLecturer.visibility = View.INVISIBLE
                    holder.tvSmallCourseLecturerLabel.visibility = View.INVISIBLE
                } else {
                    holder.tvSmallCourseLecturer.visibility = View.VISIBLE
                    holder.tvSmallCourseLecturer.text = course.courseLecturer
                    holder.tvSmallCourseLecturerLabel.visibility = View.VISIBLE
                }
                holder.tvSmallCourseAvgScr.text = ""
                for (i in 1..(floor(course.avgCourseScore).toInt())) {
                    holder.tvSmallCourseAvgScr.append("⭐")
                }
            }

            TYPE_TOP_LECTURERS -> {
                "Teacher name".also { holder.tvSmallCourseNameLabel.text = it }
                holder.tvSmallCourseName.text = course.courseLecturer

                "Lecturer score".also { holder.tvSmallCourseLecturerLabel.text = it }
                holder.tvSmallCourseLecturer.text = ""
                for (i in 1..(floor(course.avgLecturerScore).toInt()))
                    holder.tvSmallCourseLecturer.append("⭐")

                holder.tvSmallCourseAvgScr.text = ""
                val avgTeacherScore = (course.avgLecturerScore + course.avgExaminerScore) / 2
                for (i in 1..(floor(avgTeacherScore).toInt()))
                    holder.tvSmallCourseAvgScr.append("⭐")

                holder.tvBlueViewOpener.visibility = View.INVISIBLE
            }

            TYPE_TOP_EXAMINERS -> {
                "Teacher name".also { holder.tvSmallCourseNameLabel.text = it }
                holder.tvSmallCourseName.text = course.courseExaminer

                "Examiner score".also { holder.tvSmallCourseLecturerLabel.text = it }
                holder.tvSmallCourseLecturer.text = ""
                for (i in 1..(floor(course.avgExaminerScore).toInt()))
                    holder.tvSmallCourseLecturer.append("⭐")

                holder.tvSmallCourseAvgScr.text = ""
                val avgTeacherScore = (course.avgLecturerScore + course.avgExaminerScore) / 2
                for (i in 1..(floor(avgTeacherScore).toInt()))
                    holder.tvSmallCourseAvgScr.append("⭐")

                holder.tvBlueViewOpener.visibility = View.INVISIBLE
            }
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

    fun removeItem(course: Course) {
        courseList.remove(course)
        notifyDataSetChanged()
    }

    fun removeItem(id: Int) {
        courseList.removeAt(id)
        notifyDataSetChanged()
    }

    fun removeAll() {
        courseList = mutableListOf()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSmallCourseNameLabel: TextView = itemView.findViewById(R.id.tvSmallCourseNameLabel)
        val tvSmallCourseName: TextView = itemView.findViewById(R.id.tvSmallCourseName)

        val tvSmallCourseLecturer: TextView = itemView.findViewById(R.id.tvSmallCourseLecturer)
        val tvSmallCourseLecturerLabel: TextView = itemView.findViewById(R.id.tvSmallCourseLecturerLabel)

        val tvSmallCourseAvgScrLabel: TextView = itemView.findViewById(R.id.tvSmallCourseAvgScrLabel)
        val tvSmallCourseAvgScr: TextView = itemView.findViewById(R.id.tvSmallCourseAvgScr)

        val tvBlueViewOpener: TextView = itemView.findViewById(R.id.tvBlueViewOpener)

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