package sk.stuba.fei.uim.sturating.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_search_course_teachers.view.*
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
        val avgScr = (teacher.avgExaminerScore + teacher.avgLecturerScore) / 2
        for (i in 1..floor(avgScr).toInt())
            holder.tvListItemSearchTeacherRating.append("⭐")
    }

    fun addItem(teacher: Teacher) {
        teacherList.add(teacher)
        notifyDataSetChanged()
    }

    fun clear() = teacherList.clear()

    override fun getItemCount() = teacherList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvListItemSearchTeacherName: TextView = itemView.tvListItemSearchTeacherName
        val tvListItemSearchTeacherRating: TextView = itemView.tvListItemSearchTeacherRating

        var teacher: Teacher? = null
    }
}