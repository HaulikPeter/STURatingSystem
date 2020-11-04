package sk.stuba.fei.uim.sturating.ui.toplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TopListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is top list Fragment"
    }
    val text: LiveData<String> = _text
}