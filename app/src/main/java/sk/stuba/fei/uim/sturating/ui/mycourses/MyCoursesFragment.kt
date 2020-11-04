package sk.stuba.fei.uim.sturating.ui.mycourses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.uim.sturating.R

class MyCoursesFragment : Fragment() {

    private lateinit var myCoursesViewModel: MyCoursesViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        myCoursesViewModel =
                ViewModelProvider(this).get(MyCoursesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_courses, container, false)
        val textView: TextView = root.findViewById(R.id.tvMyCourses)
        myCoursesViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
}