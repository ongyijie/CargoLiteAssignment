package my.edu.tarc.cargolite

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_products.*

class Products : AppCompatActivity() {

    //Defining database
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")
    var productAdapter: ProductAdapter? = null
    val START_ADDPRODUCT_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpRecyclerView()

        val fab_add: FloatingActionButton = findViewById(R.id.fab_add)

        //Button click to show dialog
        fab_add.setOnClickListener {
            val intentAddProducts = Intent(this, AddProduct::class.java)
            startActivityForResult(intentAddProducts, START_ADDPRODUCT_REQUEST_CODE)
        }





    }//end of onCreate

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
                return defaultValue * 8   //Reduce swipe sensitivity
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_ADDPRODUCT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val message = data!!.getStringExtra("message")
                //Show snackbar
                val snackBar = Snackbar.make(
                    findViewById(R.id.ConstraintLayout), "$message", Snackbar.LENGTH_LONG
                ).setAction("Action", null)
                snackBar.show()

                productAdapter!!.notifyDataSetChanged()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}//end of class

