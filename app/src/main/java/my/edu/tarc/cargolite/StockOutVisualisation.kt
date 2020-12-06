package my.edu.tarc.cargolite


import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_stockin_visualisation.*
import java.util.*

class StockOutVisualisation : AppCompatActivity() {

    var studentList: ArrayList<PieEntry> = ArrayList()
    var quantity = Array<String>(10){"1"}
    var productID = Array<String>(10){"0"}
    var outHistory_quantity = Array<String>(10){"1"}
    var outHistory_productID = Array<String>(10){"0"}

    var counter = 0
    var outHistory_counter = 0


    var db = FirebaseFirestore.getInstance()
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stockin_visualisation)
        studentList = ArrayList()

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db.collection("products")
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        quantity[counter] = document.data.getValue("productQuantity").toString()
                        quantity[counter] = "0"
                        productID[counter] = document.data.getValue("productID").toString()
                        counter++
                    }

                } else {
                    Toast.makeText(baseContext, "Adding failed", Toast.LENGTH_SHORT).show()
                }

                db.collection("outHistory")
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                outHistory_quantity[outHistory_counter] =
                                    document.data.getValue("quantity").toString()
                                outHistory_productID[outHistory_counter] =
                                    document.data.getValue("productID").toString()
                                outHistory_counter++
                            }
                        } else {
                            Toast.makeText(
                                baseContext,
                                "outHistory adding failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        for(outhis_count in 0 until outHistory_counter){
                            for (prod_count in 0 until counter){
                                if(outHistory_productID[outhis_count] == productID[prod_count]){
                                    quantity[prod_count] = (quantity[prod_count].toInt() + outHistory_quantity[outhis_count].toInt()).toString()
                                }
                            }
                        }

                        val pieDataSet = PieDataSet(getList(),"Products")
                        val PieData = PieData(pieDataSet)
                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 170)
                        pieDataSet.valueTextColor = Color.BLACK
                        pieDataSet.valueTextSize = 16f

                        pieChart.data = PieData
                        pieChart.description.isEnabled = false
                        pieChart.centerText = "Stock Out Products"
                        pieChart.animateY(2000)
                    });
            });
    }

    fun getList(): ArrayList<PieEntry>{
        for(n in 0 until counter){
            studentList.add(PieEntry(quantity[n].toFloat(),productID[n]))
        }
        return studentList
    }

}