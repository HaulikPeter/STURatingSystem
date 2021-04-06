package sk.stuba.fei.uim.sturating.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import sk.stuba.fei.uim.sturating.R

class SearchFragment : Fragment() {

    private lateinit var etSearchText: TextView
    private lateinit var btnSearch: Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        etSearchText = root.findViewById(R.id.etSearchText)
        btnSearch = root.findViewById(R.id.btnSearch)

        btnSearch.setOnClickListener {
            if (etSearchText.text.isNotBlank()) {
                searchCourses(etSearchText.text.toString())
                etSearchText.apply {
                    visibility = View.INVISIBLE
                    isEnabled = false
                }
                btnSearch.apply {
                    visibility = View.INVISIBLE
                    isEnabled = false
                }
            } else {
                // TODO: valami figyelmezteto hogy ures a field
            }
        }

        return root
    }

    private fun searchCourses(text: String) {
        val fm = parentFragmentManager
        val ft = fm.beginTransaction()
        val fragment = SearchResultFragment(this, text)
        ft.add(R.id.fragmentSearch, fragment)
        ft.addToBackStack("B")
        ft.commit()
    }
}