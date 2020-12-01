package my.edu.tarc.cargolite.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.cargolite.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.textViewTitle)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        //Link UI to program
        val cardViewProducts: CardView = root.findViewById((R.id.cardViewProducts))
        val cardViewStockIn : CardView = root.findViewById(R.id.cardViewStockIn)
        val cardViewStockOut : CardView = root.findViewById(R.id.cardViewStockOut)
        val cardViewShipmentHistory : CardView = root.findViewById(R.id.cardViewShipmentHistory)
        val cardViewAnalytics : CardView = root.findViewById(R.id.cardViewAnalytics)
        cardViewProducts.setOnClickListener(clickListener)
        cardViewStockIn.setOnClickListener(clickListener)
        cardViewStockOut.setOnClickListener(clickListener)
        cardViewShipmentHistory.setOnClickListener(clickListener)
        cardViewAnalytics.setOnClickListener(clickListener)

        return root
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        val intentProducts = Intent(this@HomeFragment.context, Products::class.java)
        val intentStockIn = Intent(this@HomeFragment.context, StockInScanner::class.java)
        val intentStockOut = Intent(this@HomeFragment.context, StockOutScanner::class.java)
        val intentShipmentHistory = Intent(this@HomeFragment.context, ShipmentHistory::class.java)
        //val intentAnalytics = Intent(this@HomeFragment.context, Analytics::class.java)


        when (view.id) {
            R.id.cardViewProducts -> startActivity(intentProducts)
            R.id.cardViewStockIn -> startActivity(intentStockIn)
            R.id.cardViewStockOut -> startActivity(intentStockOut)
            R.id.cardViewShipmentHistory -> startActivity(intentShipmentHistory)
            //R.id.cardViewAnalytics -> startActivity(intentAnalytics)
        }
    }
}