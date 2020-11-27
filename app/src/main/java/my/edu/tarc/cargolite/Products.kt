package my.edu.tarc.cargolite

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
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
                val productName = DialogView.dialogNameEt.text.toString().trim()
                val productPrice = DialogView.dialogPriceEt.text.toString().trim()
                val productLocation = DialogView.dialogLocationEt.text.toString().trim()
                val productQuantity = DialogView.dialogQuantityEt.text.toString().trim()

                if (productID.isEmpty()) {
                    DialogView.dialogIDEt.error = "Name is required!"
                    DialogView.dialogIDEt.requestFocus()
                }

                    if (productName.isEmpty()) {
                        DialogView.dialogNameEt.error = "Name is required!"
                        DialogView.dialogNameEt.requestFocus()
                    }

                    if (productPrice.isEmpty()) {
                        DialogView.dialogPriceEt.error = "Price is required!"
                        DialogView.dialogPriceEt.requestFocus()
                    }

                    if (productLocation.isEmpty()) {
                        DialogView.dialogLocationEt.error = "Location is required!"
                        DialogView.dialogLocationEt.requestFocus()
                    }

                    if (productQuantity.isEmpty()) {
                        DialogView.dialogQuantityEt.error = "Quantity is required!"
                        DialogView.dialogQuantityEt.requestFocus()
                    }

                var flag = productID.isNotEmpty() && productName.isNotEmpty() && productPrice.isNotEmpty() && productLocation.isNotEmpty() && productQuantity.isNotEmpty()
                if (flag) {
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

                    val database = FirebaseDatabase.getInstance("https://assignment-94fa4.firebaseio.com/")
                    val data = database.getReference("product/$productID/quantity")

                    data.setValue(productQuantity)

                    //Show snackbar
                    val snackBar = Snackbar.make(
                            findViewById(R.id.ConstraintLayout), "New product added", Snackbar.LENGTH_LONG
                    ).setAction("Action", null)
                    snackBar.show()

                    //Dismiss dialog
                    mAlertDialog.dismiss()

                    productAdapter!!.notifyDataSetChanged()
                }
            }
            //Cancel button click of custom layout
            DialogView.dialogCancelBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()
            }
        }
    }//end of onCreate

   /* fun searchInDatabase(searchText: String) {
        val query: Query = collectionRef.orderBy("productName").startAt(searchText).endAt("searchText\uf8ff")
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel::class.java)
                .build()
        productAdapter = ProductAdapter(firestoreRecyclerOptions)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.adapter = productAdapter
    }*/

    fun setUpRecyclerView() {

        val query: Query = collectionRef
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel::class.java)
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

            //Credits: yoursTRULY https://www.youtube.com/watch?v=rcSNkSJ624U
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(this@Products, R.color.red))
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate()
            }
        }).attachToRecyclerView(recyclerview)
    }//end of fun setUpRecyclerView

    override fun onStart() {
        super.onStart()
        productAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        productAdapter!!.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.product_action_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.scanner -> startActivity(Intent(this,Scanner::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}//end of class

