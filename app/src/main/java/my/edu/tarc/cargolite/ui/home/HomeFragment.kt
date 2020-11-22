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
import my.edu.tarc.cargolite.Products
import my.edu.tarc.cargolite.R
import my.edu.tarc.cargolite.Scanner

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var  cardViewScanner: CardView
    private lateinit var cardViewProducts: CardView

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

        val cardViewScanner: CardView = root.findViewById(R.id.cardViewScanner)
        val cardViewProducts: CardView = root.findViewById((R.id.cardViewProducts))
        cardViewScanner.setOnClickListener(clickListener)
        cardViewProducts.setOnClickListener(clickListener)

        return root
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        val intentScanner = Intent(this@HomeFragment.context, Scanner::class.java)
        val intentProducts = Intent(this@HomeFragment.context, Products::class.java)

        when (view.id) {
            R.id.cardViewScanner -> startActivity(intentScanner)
            R.id.cardViewProducts -> startActivity(intentProducts)
        }
    }
}