package my.edu.tarc.cargolite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.dialog_addproduct.view.*


class Products : AppCompatActivity() {

    //Defining database
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")
    var productAdapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpRecyclerView()

        val fab_add: FloatingActionButton = findViewById(R.id.fab_add)
        var searchBar: SearchView = findViewById(R.id.searchbar)

        //Button click to show dialog
        fab_add.setOnClickListener {
            //Inflate the dialog with custom view
            val DialogView = LayoutInflater.from(this).inflate(R.layout.dialog_addproduct, null)
            //AlertDialogBuilder
            val myBuilder = AlertDialog.Builder(this)
                    .setView(DialogView)
                    .setTitle("Add New Product")
            //Show dialog
            val mAlertDialog = myBuilder.show()

            //Add button click of custom layout
            DialogView.dialogAddBtn.setOnClickListener {

                //Get text from EditTexts of custom layout
                val productID = DialogView.dialogIDEt.text.toString()
                val productName = DialogView.dialogNameEt.text.toString()
                val productPrice = DialogView.dialogPriceEt.text.toString()
                val productLocation = DialogView.dialogLocationEt.text.toString()
                val productQuantity = DialogView.dialogQuantityEt.text.toString()

                val product = hashMapOf(
                        "productID" to productID,
                        "productName" to productName,
                        "productPrice" to productPrice,
                        "productLocation" to productLocation,
                        "productQuantity" to productQuantity
                )

                collectionRef.document("$productID")
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

                //Dismiss dialog
                mAlertDialog.dismiss()

                //Show snackbar
                val snackBar = Snackbar.make(
                        findViewById(R.id.ConstraintLayout), "New product added", Snackbar.LENGTH_LONG
                ).setAction("Action", null)
                snackBar.show()
            }
            //Cancel button click of custom layout
            DialogView.dialogCancelBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()
            }
        }
    }

    fun setUpRecyclerView() {
        val query1: Query = collectionRef
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query1, ProductModel::class.java)
                .build()

        productAdapter = ProductAdapter(firestoreRecyclerOptions)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.adapter = productAdapter

        //Credits: Coding In FLow https://www.youtube.com/watch?v=dTuhMFP-a1g
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return defaultValue * 10   //Reduce swipe sensitivity
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val builder = android.app.AlertDialog.Builder(this@Products)
                builder.setTitle(R.string.dialogDelete)
                builder.setMessage(R.string.messageDelete)
                builder.setPositiveButton("Yes") { dialog, which ->
                    productAdapter!!.deleteItem(viewHolder.adapterPosition)
                    val snackBar = Snackbar.make(
                            findViewById(R.id.ConstraintLayout), "Product deleted", Snackbar.LENGTH_LONG
                    ).setAction("Action", null)
                    snackBar.show()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                    productAdapter!!.notifyDataSetChanged()
                }
                builder.show()
            }
        }).attachToRecyclerView(recyclerview)
    }

    override fun onStart() {
        super.onStart()
        productAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        productAdapter!!.stopListening()
    }
}

