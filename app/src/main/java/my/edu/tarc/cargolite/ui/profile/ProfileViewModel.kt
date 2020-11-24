package my.edu.tarc.cargolite.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import my.edu.tarc.cargolite.R

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        //value = ""
    }
    val text: LiveData<String> = _text


}