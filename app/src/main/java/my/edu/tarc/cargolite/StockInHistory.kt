package my.edu.tarc.cargolite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_stock_in_history.*

class StockInHistory: AppCompatActivity() {
    //Defining firestore reference
    private val myDatabase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val myCollectionRef: CollectionReference = myDatabase.collection("inHistory")

    //Global variables
    //var inAdapter : StockInHistoryAdapter? = null
    var StockInHistoryAdapter : StockInHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_in_history)

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
        val query : Query = myCollectionRef.orderBy("shipmentID", Query.Direction.DESCENDING)

        val firestoreRecyclerOptions: FirestoreRecyclerOptions<InHistoryModel> = FirestoreRecyclerOptions.Builder<InHistoryModel>()
                .setQuery(query, InHistoryModel::class.java)
                .build()

        StockInHistoryAdapter = StockInHistoryAdapter(firestoreRecyclerOptions)

        //from activity_stock_in_history
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

}//end of class