package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Visualisation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualisation)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val buttonBarChart: Button = findViewById(R.id.buttonBarChart)
        buttonBarChart.setOnClickListener{
            startActivity(Intent(this, StockOutVisualisation::class.java))

        }
        val buttonPieChart: Button = findViewById(R.id.buttonPieChart)
        buttonPieChart.setOnClickListener{
            startActivity(Intent(this, StockInVisualisation::class.java))
        }
    }
}