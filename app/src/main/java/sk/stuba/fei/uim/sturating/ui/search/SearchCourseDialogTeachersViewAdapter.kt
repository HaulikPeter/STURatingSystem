package sk.stuba.fei.uim.sturating.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.Teacher
import kotlin.math.floor

class SearchCourseDialogTeachersViewAdapter
    : RecyclerView.Adapter<SearchCourseDialogTeachersViewAdapter.ViewHolder>() {

    private var teacherList = mutableListOf<Teacher>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchCourseDialogTeachersViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_search_course_teachers, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchCourseDialogTeachersViewAdapter.ViewHolder, position: Int) {
        val teacher = teacherList[position]
        holder.teacher = teacher
        holder.tvListItemSearchTeacherName.text = teacher.name
        holder.tvListItemSearchTeacherRating.text = ""

        if (teacher.avgExaminerScore == 0.0 && teacher.avgLecturerScore == 0.0) {
            "No data to be shown".also { holder.tvListItemSearchTeacherRating.text = it }
        }
        else if (teacher.avgExaminerScore == 0.0) {
            for (i in 1..floor(teacher.avgLecturerScore).toInt())
                holder.tvListItemSearchTeacherRating.append("⭐")
        }
        else if (teacher.avgLecturerScore == 0.0) {
            for (i in 1..floor(teacher.avgExaminerScore).toInt())
                holder.tvListItemSearchTeacherRating.append("⭐")
        }
        else {
            val avgScr = (teacher.avgExaminerScore + teacher.avgLecturerScore) / 2
            for (i in 1..floor(avgScr).toInt())
                holder.tvListItemSearchTeacherRating.append("⭐")
        }
    }

    fun addItem(teacher: Teacher) {
        teacherList.add(teacher)
        notifyDataSetChanged()
    }

    fun clear() {
        teacherList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = teacherList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvListItemSearchTeacherName: TextView = itemView.findViewById(R.id.tvListItemSearchTeacherName)
        val tvListItemSearchTeacherRating: TextView = itemView.findViewById(R.id.tvListItemSearchTeacherRating)

        var teacher: Teacher? = null
    }
}