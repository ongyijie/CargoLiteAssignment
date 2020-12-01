package my.edu.tarc.cargolite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.stock_in_history_list.view.*

class StockInHistoryAdapter(options: FirestoreRecyclerOptions<InHistoryModel>):
FirestoreRecyclerAdapter<InHistoryModel, StockInHistoryAdapter.StockInHistoryAdapterViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockInHistoryAdapterViewHolder {
        return StockInHistoryAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.stock_in_history_list, parent, false))
    }

    override fun onBindViewHolder(holder: StockInHistoryAdapterViewHolder, position: Int, model: InHistoryModel) {
        holder.shipmentID.text = model.shipmentID
        holder.productID.text = model.productID
        holder.quantity.text = model.quantity
    }

    class StockInHistoryAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var shipmentID: TextView = itemView.textViewShipmentIdField
        var productID: TextView = itemView.textViewProductIdField
        var quantity: TextView = itemView.textViewProductQuantityField
    }
}