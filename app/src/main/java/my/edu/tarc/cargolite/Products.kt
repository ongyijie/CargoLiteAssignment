package my.edu.tarc.cargolite

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.product_list.*
import java.util.*

class Products : AppCompatActivity() {

    //Defining database
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")
    var productAdapter: ProductAdapter? = null
    val START_ADDPRODUCT_REQUEST_CODE = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpRecyclerView()

        val searchBar: EditText = findViewById(R.id.searchBar)
        val spinnerSearch: Spinner = findViewById(R.id.spinnerSearch)

        searchBar.setupClearButtonWithAction()
        searchBar.addTextChangedListener(object : TextWatcher {

            //Credit: Lucas Layman https://www.youtube.com/watch?v=dlwTnh22-E8
            override fun afterTextChanged(s: Editable) {
               if (s.toString().isEmpty()) {
                   val query: Query = collectionRef
                   val firestoreRecyclerOptions: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                           .setQuery(query, ProductModel::class.java)
                           .build()
                   productAdapter!!.updateOptions(firestoreRecyclerOptions)
                } else {
                   val searchBy = spinnerSearch.selectedItemPosition
                   when(searchBy) {
                       0 -> {
                           val query0: Query = collectionRef.whereEqualTo("productID", s.toString().trim().toUpperCase(Locale.ROOT))
                           val firestoreRecyclerOptions0: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                                   .setQuery(query0, ProductModel::class.java)
                                   .build()
                           productAdapter!!.updateOptions(firestoreRecyclerOptions0)
                       }
                       1 -> {
                           val query1: Query = collectionRef.whereEqualTo("productName", s.toString().trim().toUpperCase(Locale.ROOT))
                           val firestoreRecyclerOptions1: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                                   .setQuery(query1, ProductModel::class.java)
                                   .build()
                           productAdapter!!.updateOptions(firestoreRecyclerOptions1)
                       }
                       2 -> {
                           val query2: Query = collectionRef.whereEqualTo("productLocation", s.toString().trim().toUpperCase(Locale.ROOT))
                           val firestoreRecyclerOptions2: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                                   .setQuery(query2, ProductModel::class.java)
                                   .build()
                           productAdapter!!.updateOptions(firestoreRecyclerOptions2)
                       }
                   }
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

        val fab_add: FloatingActionButton = findViewById(R.id.fab_add)

        //Button click to show dialog
        fab_add.setOnClickListener {
            val intentAddProducts = Intent(this, AddProduct::class.java)
            startActivityForResult(intentAddProducts, START_ADDPRODUCT_REQUEST_CODE)
        }
    }//end of onCreate

    private fun setUpRecyclerView() {

        val query: Query = collectionRef
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel::class.java)
                .build()

        productAdapter = ProductAdapter(firestoreRecyclerOptions)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.adapter = productAdapter

        //Credit: Coding In FLow https://www.youtube.com/watch?v=dTuhMFP-a1g
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return defaultValue * 8   //Reduce swipe sensitivity
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val builder = android.app.AlertDialog.Builder(this@Products)
                builder.setCancelable(false)
                builder.setTitle(R.string.dialogDelete)
                builder.setMessage(R.string.messageDelete)
                builder.setPositiveButton("Yes") { dialog, which ->
                    productAdapter!!.deleteItem(viewHolder.adapterPosition)
                    val snackBar = Snackbar.make(
                            findViewById(R.id.ConstraintLayoutProducts), "Product deleted", Snackbar.LENGTH_LONG
                    ).setAction("Action", null)
                    snackBar.show()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                    productAdapter!!.notifyDataSetChanged()
                }
                builder.show()
            }

            //Credit: yoursTRULY https://www.youtube.com/watch?v=rcSNkSJ624U
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
        menuInflater.inflate(R.menu.product_action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.scanner -> startActivity(Intent(this, ProductScanner::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_ADDPRODUCT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val message = data!!.getStringExtra("message")
                //Show snackbar
                val snackBar = Snackbar.make(
                        findViewById(R.id.ConstraintLayoutProducts), "$message", Snackbar.LENGTH_LONG
                ).setAction("Action", null)
                snackBar.show()

                productAdapter!!.notifyDataSetChanged()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun EditText.setupClearButtonWithAction() {

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val clearIcon = if (editable?.isNotEmpty() == true) R.drawable.ic_baseline_close_24 else 0
                setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        //Credit: https://www.thinbug.com/q/6355096
        setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                    this.setText("")
                    performClick()
                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })
    }
}//end of class

