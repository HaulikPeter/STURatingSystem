package sk.stuba.fei.uim.sturating.ui.mycourses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyCoursesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is my courses Fragment"
    }
    val text: LiveData<String> = _text
}