package my.edu.tarc.cargolite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.stock_in_history_list.view.*

class StockOutHistoryAdapter(options: FirestoreRecyclerOptions<OutHistoryModel>):
FirestoreRecyclerAdapter<OutHistoryModel, StockOutHistoryAdapter.StockOutHistoryAdapterViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOutHistoryAdapter.StockOutHistoryAdapterViewHolder {
        return StockOutHistoryAdapter.StockOutHistoryAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.stock_out_history_list, parent, false))
    }

    override fun onBindViewHolder(holder: StockOutHistoryAdapter.StockOutHistoryAdapterViewHolder, position: Int, model: OutHistoryModel) {
        holder.shipmentID.text = model.shipmentID
        holder.productID.text = model.productID
        holder.quantity.text = model.quantity
        holder.date.text = model.date
        holder.time.text = model.time
    }

    class StockOutHistoryAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var shipmentID: TextView = itemView.textViewShipmentIdField
        var productID: TextView = itemView.textViewProductIdField
        var quantity: TextView = itemView.textViewProductQuantityField
        var date : TextView = itemView.textViewDateField
        var time: TextView = itemView.textViewTimeField
    }
}