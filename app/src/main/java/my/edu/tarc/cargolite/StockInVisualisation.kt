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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stockin_visualisation)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db.collection("products")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        quantity[counter] = document.data.getValue("productQuantity").toString()
                        productID[counter] = document.data.getValue("productID").toString()
                        quantity[counter] = "0"
//                            Toast.makeText(baseContext, " " + quantity[counter], Toast.LENGTH_LONG).show()
                        counter++

                    }

                } else {
                    Toast.makeText(baseContext, "products adding failed", Toast.LENGTH_SHORT).show()
                }

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

        return stockList
    }
}