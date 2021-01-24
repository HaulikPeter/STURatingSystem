package sk.stuba.fei.uim.sturating.ui.course

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.list_item_course.view.*
import sk.stuba.fei.uim.sturating.R
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
        holder.tvSmallCourseLecturer.text = course.courseLecturer
        holder.tvSmallCourseAvgScr.text = ""
        for (i in 1..(floor(course.avgCourseScore).toInt())) {
            holder.tvSmallCourseAvgScr.append("⭐")
        }
    }

    override fun getItemCount() = courseList.size

    fun addItem(course: Course) {
        courseList.add(course)
        notifyDataSetChanged()
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

/**
    private val courseNames = mutableListOf<String>()
    private fun readCourses() {
        // TODO fill courseList with data
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                courseList.clear()
                for (child in snapshot.children) {
                    courseNames.add(child.value.toString())
                }
                loadCourses()
            }

            override fun onCancelled(error: DatabaseError) { Log.w("Cannot read course list", error.toException()) }
        }
        db.child("users").child(auth.uid.toString()).child("courses")
            .addValueEventListener(listener)
    }
    private fun loadCourses() {
        for (courseName in courseNames) {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val shortName = snapshot.key
                    val longName = snapshot.child("name").value.toString()
                    val descShort = snapshot.child("description_short").value.toString()
                    val descLong = snapshot.child("description_long").value.toString()

                    // need to get teacher data
                    val l = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                        }

                        override fun onCancelled(error: DatabaseError) { Log.w("Cannot read teachers", error.toException()) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            db.child("courses").child(courseName)
                .addValueEventListener(listener)
        }
    }
*/
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