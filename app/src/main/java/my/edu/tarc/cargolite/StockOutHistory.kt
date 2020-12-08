package my.edu.tarc.cargolite

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_stock_out_history.*

class StockOutHistory: AppCompatActivity() {
    //Defining firestore reference
    private val myDatabase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val myCollectionRef: CollectionReference = myDatabase.collection("outHistory")

    //Global variables
    var outAdapter : StockOutHistoryAdapter? = null
    var StockOutHistoryAdapter : StockOutHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_out_history)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Call set Recycler view function
        setOutHistoryRecyclerView()

    }//end of OnCreate

    fun setOutHistoryRecyclerView() {
        val query : Query = myCollectionRef
                .orderBy("date", Query.Direction.DESCENDING).orderBy("time", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<OutHistoryModel> = FirestoreRecyclerOptions.Builder<OutHistoryModel>()
                .setQuery(query, OutHistoryModel::class.java)
                .build()

        StockOutHistoryAdapter = StockOutHistoryAdapter(firestoreRecyclerOptions)

        //from activity_stock_in_history
        stockOutHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        stockOutHistoryRecyclerView.adapter = StockOutHistoryAdapter
    }//end of function

    override fun onStart() {
        super.onStart()
        StockOutHistoryAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        StockOutHistoryAdapter!!.stopListening()
    }

    //Credit : Atif Pervaiz, Link : https://www.youtube.com/watch?v=fmkjH7tIyao
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.in_history_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            //display dialog to let user choose sorting method
            sortDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortDialog() {
        val TextViewSortBy: TextView = findViewById(R.id.TextViewSortBy)
        val options = arrayOf("Latest", "Oldest","ShipmentID", "ProductID")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sort By")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                Toast.makeText(baseContext, "Sort from latest.", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By Latest"
                val query0: Query = myCollectionRef
                        .orderBy("date", Query.Direction.DESCENDING).orderBy("time", Query.Direction.DESCENDING)
                val firestoreRecyclerOptions0: FirestoreRecyclerOptions<OutHistoryModel> =
                        FirestoreRecyclerOptions.Builder<OutHistoryModel>()
                                .setQuery(query0, OutHistoryModel::class.java)
                                .build()
                StockOutHistoryAdapter!!.updateOptions(firestoreRecyclerOptions0)
            } else if (which == 1) {
                Toast.makeText(baseContext, "Sort from oldest.", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By Oldest"
                val query1: Query = myCollectionRef
                        .orderBy("date", Query.Direction.ASCENDING).orderBy("time", Query.Direction.ASCENDING)
                val firestoreRecyclerOptions1: FirestoreRecyclerOptions<OutHistoryModel> =
                        FirestoreRecyclerOptions.Builder<OutHistoryModel>()
                                .setQuery(query1, OutHistoryModel::class.java)
                                .build()
                StockOutHistoryAdapter!!.updateOptions(firestoreRecyclerOptions1)
            } else if (which == 2) {
                Toast.makeText(baseContext, "Sort by ShipmentID", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By ShipmentID"
                val query2: Query = myCollectionRef
                val firestoreRecyclerOptions2: FirestoreRecyclerOptions<OutHistoryModel> =
                        FirestoreRecyclerOptions.Builder<OutHistoryModel>()
                                .setQuery(query2, OutHistoryModel::class.java)
                                .build()
                StockOutHistoryAdapter!!.updateOptions(firestoreRecyclerOptions2)
            }
            else if (which == 3) {
                Toast.makeText(baseContext, "Sort by ProductID", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By ProductID"
                val query2: Query = myCollectionRef
                        .orderBy("productID", Query.Direction.ASCENDING).orderBy("time", Query.Direction.DESCENDING)
                val firestoreRecyclerOptions2: FirestoreRecyclerOptions<OutHistoryModel> =
                        FirestoreRecyclerOptions.Builder<OutHistoryModel>()
                                .setQuery(query2, OutHistoryModel::class.java)
                                .build()
                StockOutHistoryAdapter!!.updateOptions(firestoreRecyclerOptions2)
            }
        }
        builder.create().show()
    }
}