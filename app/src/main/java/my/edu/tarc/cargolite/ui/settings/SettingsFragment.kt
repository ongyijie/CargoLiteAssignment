package my.edu.tarc.cargolite.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.cargolite.Login
import my.edu.tarc.cargolite.R

class SettingsFragment: Fragment() {
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val textView: TextView = root.findViewById(R.id.text_settings)
        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val buttonLogout: Button = root.findViewById(R.id.buttonLogout)
        buttonLogout.setOnClickListener {

            val builder = AlertDialog.Builder(this@SettingsFragment.context)
            builder.setCancelable(false)
            builder.setTitle(R.string.dialogLogout)
            builder.setMessage(R.string.messageLogout)
            builder.setPositiveButton("Yes") { dialog, which ->

                val intentLogin = Intent(this@SettingsFragment.context, Login::class.java)
                intentLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK    //Prevent back button after logout
                startActivity(intentLogin)
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }
        return root
    }
}