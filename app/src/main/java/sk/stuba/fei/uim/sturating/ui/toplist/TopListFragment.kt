package sk.stuba.fei.uim.sturating.ui.toplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_my_courses_filled.*
import sk.stuba.fei.uim.sturating.R
import sk.stuba.fei.uim.sturating.ui.course.CourseViewAdapter
import sk.stuba.fei.uim.sturating.ui.mycourses.MyCoursesFragment

class TopListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var topListViewModel: TopListViewModel
    private lateinit var rvTopList: RecyclerView
    private lateinit var courseViewAdapter: CourseViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_top_list, container, false)

        val spinner: Spinner = root.findViewById(R.id.spinnerTopList)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        //TODO: here we could ask Firebase about the list
        rvTopList = root.findViewById(R.id.rvTopList)
        courseViewAdapter = CourseViewAdapter()
        //TODO: error here
        //courseViewAdapter.itemClickLister = MyCoursesFragment()

        rvTopList.adapter = courseViewAdapter

        return root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val list = resources.getStringArray(R.array.spinner_array)
        when(parent?.getItemAtPosition(position)) {
            list[0] -> onSelectCourses()
            list[1] -> onSelectLecturers()
            list[2] -> onSelectExaminers()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun onSelectCourses() {
        //val asd = "ASD"
    }

    private fun onSelectLecturers() {

    }

    private fun onSelectExaminers() {

    }
}