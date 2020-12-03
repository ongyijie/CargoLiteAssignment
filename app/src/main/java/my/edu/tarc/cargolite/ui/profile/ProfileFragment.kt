package my.edu.tarc.cargolite.ui.profile

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.edit_password_dialog.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import my.edu.tarc.cargolite.R
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    //Global variable
    private lateinit var profileViewModel: ProfileViewModel
    private val REQUEST_IMAGE_CAPTURE = 100
    private lateinit var imageUri : Uri
    private val currentUser = FirebaseAuth.getInstance().getCurrentUser()

    //Defining database
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val profileRef: CollectionReference = database.collection("users")

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
        val textViewUsername : TextView = root.findViewById(R.id.textViewUsername)
        val textViewEmail : TextView = root.findViewById(R.id.textViewEmail)
        val textViewPosition : TextView = root.findViewById(R.id.textViewPosition)
        val textViewPhone : TextView = root.findViewById(R.id.textViewPhone)
        val imageViewProfilePic : ImageView = root.findViewById(R.id.imageViewProfilePic)


        //Defining firebase
        val firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val currentUserEmail = firebaseUser?.email.toString()
        //Retrieve from cloud firestore the current user's profile
        database.collection("users")
            .whereEqualTo("email", currentUserEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    textViewUsername.setText(document.getString("userName"))
                    textViewEmail.setText(document.getString("email"))
                    textViewPosition.setText(document.getString("position"))
                    textViewPhone.setText(document.getString("phone"))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }

        //read the photo of current user stored in storage
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        storageRef.downloadUrl.addOnCompleteListener { urlTask ->
            if(urlTask.isSuccessful) {
                urlTask.result?.let {
                    imageUri = it
                    Glide.with(this).load(imageUri).into(imageViewProfilePic)
                }
            }
            else {
                Toast.makeText(getActivity(), "Set up your profile picture now.", Toast.LENGTH_SHORT).show()
            }
        }

        //imageView onClickListener -- let user take photo
        imageViewProfilePic.setOnClickListener {
            takePictureIntent()
        }

        //Edit button onClick show user editProfile dialog
        fab_editProfile.setOnClickListener {
            //inflate layout for dialog
            val DialogView = LayoutInflater.from(getActivity()).inflate(R.layout.edit_password_dialog, null)

            //Link UI from dialog to the program
            val passwordOld: EditText = DialogView.findViewById(R.id.passwordOld)
            val passwordNew: EditText = DialogView.findViewById(R.id.passwordNew)
            val passwordConfirm: EditText = DialogView.findViewById(R.id.passwordConfirm)

            //AlertDialogBuilder
            val myBuilder = AlertDialog.Builder(getActivity())
                .setCancelable(false)
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
                if (TextUtils.isEmpty(pswNew) ) {
                    passwordNew.error = "This is a required field"
                    passwordNew.requestFocus()
                    isValid = "false"
                }
                if (pswNew.length <= 6) {
                    passwordNew.error = "Password must be at least 6 characters"
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
                    //Update to firebase
                    updatePassword(pswOld,pswNew)
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

    //Credits: Simplified Coding, Link : https://www.youtube.com/watch?v=-Kuhqq2ipXM&t=304s
    //upload profile photo to firebase storage
    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap : Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()
        val upload = storageRef.putBytes(image)

        progressbar_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener{ uploadTask ->
            if(uploadTask.isSuccessful) {
                Toast.makeText(getActivity(),"Picture uploaded", Toast.LENGTH_SHORT).show()
                imageViewProfilePic.setImageBitmap(bitmap)
                progressbar_pic.visibility = View.GONE
            }else {
                uploadTask.exception?.let {
                    Toast.makeText(getActivity(), "Upload Picture Failed. Please try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Credits: Atif Pervaiz, Link : https://www.youtube.com/watch?v=IyBSlDUCJOA&t=602s
    //Update password
    fun updatePassword(pswOld: String, pswNew: String) {
        val firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val authCredential = EmailAuthProvider.getCredential(firebaseUser?.email.toString(), pswOld)
        firebaseUser!!.reauthenticate(authCredential)
            .addOnSuccessListener { task ->
                //Re-authenticate success
                firebaseUser.updatePassword(pswNew)
                    .addOnSuccessListener {
                        //password updated at firebase
                        Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(getActivity(), "Update Failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                //Re-authenticate failed
                Toast.makeText(getActivity(), "Authentication Failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateProfile(userName:String, position: String, phone: String) {
        val check = userName.isNotEmpty() && phone.toString().isNotEmpty() && position.toString().isNotEmpty()
        if (check) {
            val users = hashMapOf(
                "userName" to userName,
                "phone" to phone,
                "position" to position
            )
            profileRef.document("$userName").set(users)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }
    }
}//end of class


