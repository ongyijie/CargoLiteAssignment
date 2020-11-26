package my.edu.tarc.cargolite.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.edit_profile_dialog.view.*
import my.edu.tarc.cargolite.R

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

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
        val fab_editProfile: FloatingActionButton = root.findViewById(R.id.fab_editProfile)

        //Edit button onClick show user editProfile dialog
        fab_editProfile.setOnClickListener {
            //inflate layout for dialog
            val DialogView = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_dialog, null)

            //Link UI from dialog to the program
            val passwordOld: EditText = DialogView.findViewById(R.id.passwordOld)
            val passwordNew: EditText = DialogView.findViewById(R.id.passwordNew)
            val passwordConfirm: EditText = DialogView.findViewById(R.id.passwordConfirm)

            //AlertDialogBuilder
            val myBuilder = AlertDialog.Builder(getActivity())
                    .setView(DialogView)

            //Show dialog
            var dialog = myBuilder.show()

            DialogView.buttonUpdate.setOnClickListener {
                //Retrieve text from EditText
                val pswOld = passwordOld.text.toString()
                val pswNew = passwordNew.text.toString()
                val pswCon = passwordConfirm.text.toString()
                var isValid = "true"

                if (TextUtils.isEmpty(pswOld)) {
                    passwordOld.error = "This is a required field"
                    passwordOld.requestFocus()
                    isValid = "false"
                }
                if (TextUtils.isEmpty(pswNew)) {
                    passwordNew.error = "This is a required field"
                    passwordNew.requestFocus()
                    isValid = "false"
                }
                if (TextUtils.isEmpty(pswCon)) {
                    passwordConfirm.error = "This is a required field"
                    passwordConfirm.requestFocus()
                    isValid = "false"
                }
                if (pswNew == pswOld) {
                    passwordNew.error = "New password cannot be the same as Current Password"
                    passwordNew.requestFocus()
                    isValid = "false"
                }
                if (pswCon != pswNew) {
                    passwordConfirm.error = "Password does not match"
                    passwordConfirm.requestFocus()
                    isValid = "false"
                }
                if (isValid == "true") {
                    //Update to firestore

                    Toast.makeText(getActivity(), "Validation Success", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(getActivity(), "Validation Failed", Toast.LENGTH_SHORT).show()
                }
            }
            DialogView.buttonCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        return root
    }//end of View
/*
    fun updatePassword() {
        //var user = firebase.auth().currentUser;
        //var newPassword = getASecureRandomPassword();

        //user.updatePassword(newPassword).then(function() {
            // Update successful.
        //}).catch(function(error) {
            // An error happened.
        //});
        Toast.makeText(getActivity(), "Validation Success", Toast.LENGTH_SHORT).show()
    }
 */
}//end of class


/*
//Inflate edit Profile dialog with a custom view
            val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_profile_dialog, null)
            //start of alert dialog builder
            val editBuilder = AlertDialog.Builder(this).setView(dialogView).setTitle("Edit Profile")
            //display dialog
            editBuilder.show()
----------------------------------------------------------
var user = firebase.auth().currentUser;
                        var newPassword = getASecureRandomPassword();

                        user.updatePassword(newPassword).then(function() {
                            // Update successful.
                        }).catch(function(error) {
                            // An error happened.
                        });
 */