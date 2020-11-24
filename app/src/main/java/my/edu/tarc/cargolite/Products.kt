package my.edu.tarc.cargolite

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
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
                    .setTitle("Add New Product")
            //Show dialog
            val  mAlertDialog = myBuilder.show()
            val productList = ArrayList<String>(5)

            val textViewProduct: TextView = findViewById(R.id.textViewProduct)
            //Add button click of custom layout
            DialogView.dialogAddBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()

                //Show snackbar
                val snackBar = Snackbar.make(findViewById(R.id.ConstraintLayout), "New product added", Snackbar.LENGTH_LONG
                ).setAction("Action", null)
                snackBar.show()

                //Get text from EditTexts of custom layout
                val product_id = DialogView.dialogIDEt.text.toString()
                val product_name = DialogView.dialogNameEt.text.toString()
                val unit_price = DialogView.dialogPriceEt.text.toString()
                val stock_location = DialogView.dialogLocationEt.text.toString()
                val quantity = DialogView.dialogQuantityEt.text.toString()

                textViewProduct.text = String.format("%20s %10s \n%20s %10s \n%20s %10s \n%20s %10s \n%20s %10s", "Product ID: ", product_id, "Product Name: ", product_name, "Unit price (RM) :", unit_price, "Stock location :", stock_location, "Quantity: ", quantity)
            }

            //Cancel button click of custom layout
            DialogView.dialogCancelBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()
            }
        }
    }
}

