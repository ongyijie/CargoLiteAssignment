package my.edu.tarc.cargolite

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.dialog_addproduct.view.*

class Products: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        val fab_add: FloatingActionButton = findViewById(R.id.fab_add)

        //Button click to show dialog
        fab_add.setOnClickListener {
            //Inflate the dialog with custom view
            val DialogView = LayoutInflater.from(this).inflate(R.layout.dialog_addproduct, null)
            //AlertDialogBuilder
            val myBuilder = AlertDialog.Builder(this)
                    .setView(DialogView)
                    .setTitle("Add Product")
            //show dialog
            val  mAlertDialog = myBuilder.show()
            //Add button click of custom layout
            DialogView.dialogAddBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()
                //Get text from EditTexts of custom layout
                val id = DialogView.dialogIDEt.text.toString()
                val name = DialogView.dialogNameEt.text.toString()
                val location = DialogView.dialogLocationEt.text.toString()
                val quantity = DialogView.dialogQuantityEt.text.toString()

                //Set the input text in TextView
                //mainInfoTv.setText("Name:"+ name +"\nEmail: "+ email +"\nPassword: "+ password)
            }
            //Cancel button click of custom layout
            DialogView.dialogCancelBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()
            }
        }
    }
}