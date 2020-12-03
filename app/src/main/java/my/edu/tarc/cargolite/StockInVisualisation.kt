package my.edu.tarc.cargolite

import android.graphics.Color.BLACK
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_stockin_visualisation.*


class StockInVisualisation : AppCompatActivity() {

    var stockList: ArrayList<PieEntry> = ArrayList()
    var quantity = Array<String>(10){"1"}
    var productID = Array<String>(10){"0"}
    var inHistory_quantity = Array<String>(10){"1"}
    var inHistory_productID = Array<String>(10){"0"}

    var counter = 0
    var inHistory_counter = 0

    var db = FirebaseFirestore.getInstance()
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
//    private val collectionRef: CollectionReference = database.collection("products")
//    private val InHistoryRef: CollectionReference = database.collection("inHistory")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stockin_visualisation)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        var num0 = 0f
//        var num1 = 0f

//        studentList = ArrayList()
//        quantity = ArrayList()

//        writeData()
//        val docRef: DocumentReference = db.collection("users").document("U000003")
//        docRef.get().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val document = task.result
//                if (document != null) {
//                    Log.i("LOGGER", "First " + document.getString("email"))
//                    Log.i("LOGGER", "Last " + document.getString("position"))
//                    Log.i("LOGGER", "Born " + document.getString("username"))
//                } else {
//                    Log.d("LOGGER", "No such document")
//                }
//            } else {
//                Log.d("LOGGER", "get failed with ", task.exception)
//            }
//        }
        db.collection("products")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                if (task.isSuccessful) {
                    for (document in task.result!!) {

//                            Toast.makeText(baseContext, " " + document.data.getValue("quantity"), Toast.LENGTH_LONG).show()
//                            result.append(document.data.getValue("username")).append(" ").append(document.data.getValue("lastName").append(" "))
//                            Toast.makeText(baseContext, result, Toast.LENGTH_LONG).show()
                        quantity[counter] = document.data.getValue("productQuantity").toString()
                        productID[counter] = document.data.getValue("productID").toString()
//                            Toast.makeText(baseContext, " " + quantity[counter], Toast.LENGTH_LONG).show()
                        counter++

                    }

                } else {
                    Toast.makeText(baseContext, "products adding failed", Toast.LENGTH_SHORT).show()
                }
//                    num0 = quantity[0].toFloat()
//                    num1 = quantity[1].toFloat()
//
//                    Toast.makeText(baseContext, " " + num0, Toast.LENGTH_LONG).show()
//                    Toast.makeText(baseContext, " " + num1, Toast.LENGTH_LONG).show()

                db.collection("inHistory")
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                inHistory_quantity[inHistory_counter] =
                                    document.data.getValue("quantity").toString()
                                inHistory_productID[inHistory_counter] =
                                    document.data.getValue("productID").toString()
                                inHistory_counter++
                            }
                        } else {
                            Toast.makeText(
                                baseContext,
                                "inHistory adding failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        for(inhis_count in 0 until inHistory_counter){
                            for (prod_count in 0 until counter){
                                if(inHistory_productID[inhis_count] == productID[prod_count]){
                                    quantity[prod_count] = (quantity[prod_count].toInt() + inHistory_quantity[inhis_count].toInt()).toString()
                                }
                            }
                        }


                        val pieDataSet = PieDataSet(getList(),"Products")
                        val PieData = PieData(pieDataSet)
                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 170)
                        pieDataSet.valueTextColor = BLACK
                        pieDataSet.valueTextSize = 16f


                        pieChart.data = PieData
                        pieChart.description.isEnabled = false
                        pieChart.centerText = "Stock In Products"
                        pieChart.animateY(2000)
                    });
            });
    }

    fun getList(): ArrayList<PieEntry>{
        for(n in 0 until counter){
            stockList.add(PieEntry(quantity[n].toFloat(),productID[n]))
        }
//        stockList.add(PieEntry(quantity[0].toFloat(), productID[0]))
//        stockList.add(PieEntry(quantity[1].toFloat(), productID[1]))
//        stockList.add(PieEntry(quantity[2].toFloat(), productID[2]))
//        stockList.add(PieEntry(quantity[3].toFloat(), productID[3]))
        return stockList
    }

//    fun writeData(){
//        val user: MutableMap<String, Any> = HashMap()
//        user["first"] = "Ada"
//        user["last"] = "Lovelace"
//        user["born"] = 1815
//
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener { Toast.makeText(baseContext, "Added successfully", Toast.LENGTH_SHORT).show() }
//                .addOnFailureListener { Toast.makeText(baseContext, "Adding failed", Toast.LENGTH_SHORT).show() }
//    }

//    fun loadData(){
//        db.collection("inHistory")
//                .get()
//                .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
//
//                    var counter = 0
//                    if (task.isSuccessful) {
//                        for (document in task.result!!) {
//
//                            Toast.makeText(baseContext, " " + document.data.getValue("quantity"), Toast.LENGTH_LONG).show()
////                            result.append(document.data.getValue("username")).append(" ").append(document.data.getValue("lastName").append(" "))
////                            Toast.makeText(baseContext, result, Toast.LENGTH_LONG).show()
//                            quantity[counter] = document.data.getValue("quantity").toString()
//                            counter++
//
//
//                        }
//
//                    } else {
//                        Toast.makeText(baseContext, "Adding failed", Toast.LENGTH_SHORT).show()
//                    }
//
//                });




}