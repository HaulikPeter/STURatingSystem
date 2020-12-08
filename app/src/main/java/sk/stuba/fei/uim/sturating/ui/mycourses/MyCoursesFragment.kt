package sk.stuba.fei.uim.sturating.ui.mycourses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_my_courses_filled.*
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.Course
import sk.stuba.fei.uim.sturating.ui.course.CourseViewAdapter
import sk.stuba.fei.uim.sturating.ui.course.CourseFragment

class MyCoursesFragment : Fragment(), CourseViewAdapter.CourseItemClickListener {

    private lateinit var myCoursesViewModel: MyCoursesViewModel
    private lateinit var courseViewAdapter: CourseViewAdapter
    private lateinit var rvCourses: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //TODO: if list is empty, inflate empty
        //val root = inflater.inflate(R.layout.fragment_my_courses_empty, container, false)
        //val textView: TextView = root.findViewById(R.id.tvMyCourses)
        //myCoursesViewModel.text.observe(viewLifecycleOwner, {
        //    textView.text = it
        //})
        //root.findViewById<Button>(R.id.btnFragment).setOnClickListener { click() }

        //TODO: else inflate filled fragment
        val root = inflater.inflate(R.layout.fragment_my_courses_filled, container, false)
        rvCourses = root.findViewById(R.id.rvCourses)
        courseViewAdapter = CourseViewAdapter(requireContext())
        courseViewAdapter.itemClickLister = this

        rvCourses.adapter = courseViewAdapter

        return root
    }

    override fun onItemClick(course: Course) {
        //TODO: open course as dialog fragment
        val fm = parentFragmentManager
        val fragment = CourseFragment(course)
        fragment.show(fm, "Course Fragment")
    }
}