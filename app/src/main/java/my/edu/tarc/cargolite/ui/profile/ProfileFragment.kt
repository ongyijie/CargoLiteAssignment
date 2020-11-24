package my.edu.tarc.cargolite.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import my.edu.tarc.cargolite.R

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var status : String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        profileViewModel =
                ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val textView: TextView = root.findViewById(R.id.text_profile)
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        //Link UI to program
        val fab_editProfile : FloatingActionButton = root.findViewById(R.id.fab_editProfile)
        val passwordOld : TextView = root.findViewById(R.id.passwordOld)
        val passwordNew : TextView = root.findViewById(R.id.passwordNew)
        val passwordConfirm : TextView = root.findViewById(R.id.passwordConfirm)

        //Edit button onClick show user editProfile dialog
        fab_editProfile.setOnClickListener() {
            // create an alert builder
            val builder = androidx.appcompat.app.AlertDialog.Builder(it.context, R.style.MyDialogTheme)
            builder.setTitle("Edit Profile")
            // set the custom layout
            val customLayout: View = layoutInflater.inflate(R.layout.edit_profile_dialog, null);
            builder.setView(customLayout);

            builder.apply {
                setPositiveButton("Update", DialogInterface.OnClickListener { dialog, _ ->
                    //When user click update, update the database
                    //code for update database
                    //validate password update
                        val pswOld = passwordOld.text.toString()
                        val pswNew = passwordNew.text.toString()
                        val pswCon = passwordConfirm.text.toString()

                        if (TextUtils.isEmpty(pswOld)) {
                            passwordOld.error = "This is a required field"
                            status = "false"
                        }
                        if (TextUtils.isEmpty(pswNew)) {
                            passwordNew.error = "This is a required field"
                            status = "false"
                        }
                        if (pswNew == pswOld) {
                            passwordNew.error = "New password cannot be the same as old password"
                            status = "false"
                        }
                        if (pswCon != pswNew) {
                            passwordConfirm.error = "Password doesn't match!"
                            status = "false"
                        }
                    //close the dialog
                    dialog.cancel()
                })
                setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _->
                    //When user click cancel,close the dialog
                    dialog.cancel()
                })
            }
            // create and show the alert dialog
            val dialog: androidx.appcompat.app.AlertDialog = builder.create()
            dialog.show()
        }

        return root
    }//end of View

}//end of class
/*
//Inflate edit Profile dialog with a custom view
            val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_profile_dialog, null)
            //start of alert dialog builder
            val editBuilder = AlertDialog.Builder(this).setView(dialogView).setTitle("Edit Profile")
            //display dialog
            editBuilder.show()
 */