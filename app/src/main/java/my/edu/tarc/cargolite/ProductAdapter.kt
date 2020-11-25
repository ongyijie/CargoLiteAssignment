//Credits: larn tech https://www.youtube.com/watch?v=c8lfcBYlaC4

package my.edu.tarc.cargolite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.product_list.view.*

class ProductAdapter(options: FirestoreRecyclerOptions<ProductModel>):
FirestoreRecyclerAdapter<ProductModel, ProductAdapter.ProductAdapterViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapterViewHolder {
        return ProductAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_list, parent, false))
    }

    fun deleteItem(position: Int) {
        snapshots.getSnapshot(position).reference.delete()
    }

    override fun onBindViewHolder(holder: ProductAdapterViewHolder, position: Int, model: ProductModel) {
        holder.productID.text = model.productID
        holder.productName.text = model.productName
        holder.productPrice.text = ("Price (RM): " + model.productPrice)
        holder.productLocation.text = ("Location: " + model.productLocation)
        holder.productQuantity.text = ("Quantity: " + model.productQuantity)
    }

    class ProductAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var productID: TextView = itemView.textViewID
        var productName: TextView = itemView.textViewName
        var productPrice: TextView = itemView.textViewPrice
        var productLocation: TextView = itemView.textViewLocation
        var productQuantity: TextView = itemView.textViewQuantity
    }
}