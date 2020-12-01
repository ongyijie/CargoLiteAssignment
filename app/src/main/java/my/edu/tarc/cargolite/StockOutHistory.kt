package my.edu.tarc.cargolite

import android.os.Bundle
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

        //Call set Recycler view function
        setOutHistoryRecyclerView()
    }//end of OnCreate

    fun setOutHistoryRecyclerView() {
        //ltr try orderby timestamp
        val query : Query = myCollectionRef.orderBy("shipmentID", Query.Direction.DESCENDING)

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
}