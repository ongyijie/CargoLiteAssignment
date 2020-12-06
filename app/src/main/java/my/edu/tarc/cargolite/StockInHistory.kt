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
import kotlinx.android.synthetic.main.activity_stock_in_history.*

class StockInHistory : AppCompatActivity() {
    //Defining firestore reference
    private val myDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val myCollectionRef: CollectionReference = myDatabase.collection("inHistory")

    //Global variables
    var StockInHistoryAdapter: StockInHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_in_history)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Call set Recycler view function
        setInHistoryRecyclerView()


    }//end of OnCreate

    /* Reference :
    - Coding In Flow, Link : https://www.youtube.com/watch?v=lAGI6jGS4vs&t=486s
    - Quick code, Link : https://medium.com/quick-code/display-data-from-firebase-firestore-in-android-recyclerview-db39f8c7d6b
    - larn tech, Link : https://www.youtube.com/watch?v=c8lfcBYlaC4
    */
    //Create function to setup Recycler View
    fun setInHistoryRecyclerView() {
        //ltr try orderby timestamp
        val query: Query = myCollectionRef.orderBy("time", Query.Direction.DESCENDING)

        val firestoreRecyclerOptions: FirestoreRecyclerOptions<InHistoryModel> =
                FirestoreRecyclerOptions.Builder<InHistoryModel>()
                        .setQuery(query, InHistoryModel::class.java)
                        .build()

        StockInHistoryAdapter = StockInHistoryAdapter(firestoreRecyclerOptions)

        stockInHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        stockInHistoryRecyclerView.adapter = StockInHistoryAdapter
    }//end of function

    override fun onStart() {
        super.onStart()
        StockInHistoryAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        StockInHistoryAdapter!!.stopListening()
    }

    //Credits : Atif Pervaiz, Link : https://www.youtube.com/watch?v=fmkjH7tIyao
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
                Toast.makeText(baseContext, "Sort By latest.", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By Latest"
                val query0: Query = myCollectionRef
                        .orderBy("date", Query.Direction.DESCENDING).orderBy("time", Query.Direction.DESCENDING)
                val firestoreRecyclerOptions0: FirestoreRecyclerOptions<InHistoryModel> =
                        FirestoreRecyclerOptions.Builder<InHistoryModel>()
                                .setQuery(query0, InHistoryModel::class.java)
                                .build()
                StockInHistoryAdapter!!.updateOptions(firestoreRecyclerOptions0)
            } else if (which == 1) {
                Toast.makeText(baseContext, "Sort by oldest.", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By Oldest"
                val query1: Query = myCollectionRef
                        .orderBy("date", Query.Direction.ASCENDING).orderBy("time", Query.Direction.ASCENDING)
                val firestoreRecyclerOptions1: FirestoreRecyclerOptions<InHistoryModel> =
                        FirestoreRecyclerOptions.Builder<InHistoryModel>()
                                .setQuery(query1, InHistoryModel::class.java)
                                .build()
                StockInHistoryAdapter!!.updateOptions(firestoreRecyclerOptions1)
            } else if (which == 2) {
                Toast.makeText(baseContext, "Sort By ShipmentID", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By ShipmentID"
                val query2: Query = myCollectionRef
                val firestoreRecyclerOptions2: FirestoreRecyclerOptions<InHistoryModel> =
                        FirestoreRecyclerOptions.Builder<InHistoryModel>()
                                .setQuery(query2, InHistoryModel::class.java)
                                .build()
                StockInHistoryAdapter!!.updateOptions(firestoreRecyclerOptions2)
            }else if (which == 3) {
                Toast.makeText(baseContext, "Sort by ProductID", Toast.LENGTH_SHORT).show()
                TextViewSortBy.text = "Sort By ProductID"
                val query2: Query = myCollectionRef
                        .orderBy("productID", Query.Direction.ASCENDING).orderBy("time", Query.Direction.DESCENDING)
                val firestoreRecyclerOptions2: FirestoreRecyclerOptions<InHistoryModel> =
                        FirestoreRecyclerOptions.Builder<InHistoryModel>()
                                .setQuery(query2, InHistoryModel::class.java)
                                .build()
                StockInHistoryAdapter!!.updateOptions(firestoreRecyclerOptions2)
            }
        }
        builder.create().show()
    }
}//end of class