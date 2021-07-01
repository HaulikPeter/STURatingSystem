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

// adapter for the courses recyclerview
class CourseViewAdapter() : RecyclerView.Adapter<CourseViewAdapter.ViewHolder>() {

    companion object {
        const val TYPE_NORMAL = 0
        const val TYPE_TOP_LECTURERS = 1
        const val TYPE_TOP_EXAMINERS = 2
    }

    private var type = TYPE_NORMAL

    // it has types for the top list to reduce code redundancy
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

    // sets the the view of the type depending on the type set
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
                var avgTeacherScore = course.avgLecturerScore
                if (course.avgExaminerScore != 0.0)
                    avgTeacherScore = (course.avgLecturerScore + course.avgExaminerScore) / 2
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
                var avgTeacherScore = course.avgExaminerScore
                if (course.avgLecturerScore != 0.0)
                    avgTeacherScore = (course.avgLecturerScore + course.avgExaminerScore) / 2
                for (i in 1..(floor(avgTeacherScore).toInt()))
                    holder.tvSmallCourseAvgScr.append("⭐")

                holder.tvBlueViewOpener.visibility = View.INVISIBLE
            }
        }
    }

    // returns the intem count in the list
    override fun getItemCount() = courseList.size

    // checks the list if its empty
    fun isEmpty() = courseList.isEmpty()

    // checks for an instance of a item with its id
    fun hasItem(id: Int): Boolean {
        return try {
            courseList[id]
            true
        } catch (e: IndexOutOfBoundsException) {
            Log.w("Element not exists", e.toString())
            false
        }
    }

    // checks for an instance of a item with its name
    fun hasItem(courseName: String): Boolean {
        courseList.forEach{
            if (it.shortName === courseName)
                return true
        }
        return false
    }

    // adds an item to the list
    fun addItem(course: Course) {
        if (!courseList.contains(course)) {
            courseList.add(course)
            notifyDataSetChanged()
        }
    }

    // replaces the items in the list
    fun addAllItem(mutableList: MutableList<Course>) {
        courseList.clear()
        courseList = mutableList
        notifyDataSetChanged()
    }

    // replaces a specific item in the list
    fun replaceItem(course: Course) {
        if (courseList.remove(course)) {
            courseList.add(course)
            notifyDataSetChanged()
        }
    }

    // removes an item from the list
    fun removeItem(course: Course) {
        courseList.remove(course)
        notifyDataSetChanged()
    }

    // removes an item by its id from the list
    fun removeItem(id: Int) {
        courseList.removeAt(id)
        notifyDataSetChanged()
    }

    // clears the list
    fun removeAll() {
        courseList = mutableListOf()
        notifyDataSetChanged()
    }

    // viewholder class for the view holder
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