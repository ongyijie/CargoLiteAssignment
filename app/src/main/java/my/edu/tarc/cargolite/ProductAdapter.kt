//Credit: larn tech https://www.youtube.com/watch?v=c8lfcBYlaC4

package my.edu.tarc.cargolite

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_editproduct.view.*
import kotlinx.android.synthetic.main.product_list.view.*
import kotlinx.android.synthetic.main.product_list.view.textViewName
import java.util.*

class ProductAdapter(options: FirestoreRecyclerOptions<ProductModel>):
FirestoreRecyclerAdapter<ProductModel, ProductAdapter.ProductAdapterViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapterViewHolder {
        return ProductAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_list, parent, false))
    }

    fun deleteItem(position: Int) {
        snapshots.getSnapshot(position).reference.delete()
    }

    override fun onBindViewHolder(holder: ProductAdapterViewHolder, position: Int, model: ProductModel) {
        holder.productID.text = model.productID
        holder.productName.text = model.productName
        holder.productPrice.text = model.productPrice
        holder.productLocation.text = model.productLocation
        holder.productQuantity.text = model.productQuantity

        //Credit: iOS Lifee https://stackoverflow.com/a/51007705
        holder.itemView.setOnLongClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_editproduct, null)

            val myBuilder = AlertDialog.Builder(holder.itemView.context)
                    .setView(dialogView)
                    .setCancelable(false)

            val mAlertDialog = myBuilder.show()

            dialogView.dialogID.setText(holder.productID.text)
            dialogView.dialogName.setText(holder.productName.text)
            dialogView.dialogPrice.setText(holder.productPrice.text)
            dialogView.dialogLocation.setText(holder.productLocation.text)

            dialogView.saveBtn.setOnClickListener {

                //Get text from EditTexts of custom layout
                val productID = dialogView.dialogID.text.toString()
                val productName = dialogView.dialogName.text.toString()
                val productPrice = dialogView.dialogPrice.text.toString()
                val productLocation = dialogView.dialogLocation.text.toString()

                if (productName.isEmpty()) {
                    dialogView.dialogName.error = "Name is required!"
                    dialogView.dialogName.requestFocus()
                }

                if (productPrice.isEmpty()) {
                    dialogView.dialogPrice.error = "Price is required!"
                    dialogView.dialogPrice.requestFocus()
                }

                if (productLocation.isEmpty()) {
                    dialogView.dialogLocation.error = "Location is required!"
                    dialogView.dialogLocation.requestFocus()
                }

                val flag =
                        productName.isNotEmpty() && productPrice.isNotEmpty() && productLocation.isNotEmpty()

                if (flag) {
                    //Defining database
                    val database = Firebase.firestore

                    val product = hashMapOf(
                            "productID" to productID,
                            "productName" to productName.toUpperCase(Locale.ROOT),
                            "productPrice" to productPrice,
                            "productLocation" to productLocation.toUpperCase(Locale.ROOT),
                        "productQuantity" to  holder.productQuantity.text
                    )

                    database.collection("products").document(productID)
                            .set(product)
                            .addOnSuccessListener { documentReference ->
                                Log.d("TAG", "DocumentSnapshot added")
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error adding document", e)
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                            }

                    Toast.makeText(holder.itemView.context, "Product details updated", Toast.LENGTH_LONG).show()
                    mAlertDialog.dismiss()
                }
            }
            dialogView.cancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
            }
            return@setOnLongClickListener true
        }
    }

    class ProductAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var productID: TextView = itemView.editTextID
        var productName: TextView = itemView.textViewName
        var productPrice: TextView = itemView.textViewPrice
        var productLocation: TextView = itemView.textViewLocation
        var productQuantity: TextView = itemView.textViewQuantity
    }
}