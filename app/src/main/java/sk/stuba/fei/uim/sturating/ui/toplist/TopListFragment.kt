package sk.stuba.fei.uim.sturating.ui.toplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.uim.sturating.R

class TopListFragment : Fragment() {

    private lateinit var topListViewModel: TopListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        topListViewModel =
            ViewModelProvider(this).get(TopListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_top_list, container, false)
        val textView: TextView = root.findViewById(R.id.tvTopList)
        topListViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
}