package my.edu.tarc.cargolite

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.insert_alert_layout.view.*
import my.edu.tarc.cargolite.NotificationApp.Companion.CHANNEL_1_ID
import my.edu.tarc.cargolite.NotificationApp.Companion.CHANNEL_2_ID
import my.edu.tarc.cargolite.NotificationApp.Companion.CHANNEL_3_ID
import my.edu.tarc.cargolite.NotificationApp.Companion.CHANNEL_4_ID


class Notification : AppCompatActivity(){
    var triggerQuantity = Array<String>(10){"1"}
    var triggerProductName = Array<String>(10){"0"}
    var insertNotificationProductName = Array<String>(10){"0"}
    var productName = Array<String>(10){"0"}
    var quantity = Array<String>(10){"1"}
    var productNameFlag = false
    var quantityIntFlag = false
    private val KEY_PRODUCT_NAME = "productName"
    private val KEY_PRODUCT_QUANTITY = "productQuantity"



    private val notificationList = generateDummyList(1)
    private val adapter = NotificationAdapter(notificationList)
    private val db = FirebaseFirestore.getInstance()
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationManager = NotificationManagerCompat.from(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        //Credits: Coding In FLow https://www.youtube.com/watch?v=dTuhMFP-a1g
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return defaultValue * 8   //Reduce swipe sensitivity
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notificationList.removeAt(viewHolder.adapterPosition)
                adapter.notifyDataSetChanged()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(ContextCompat.getColor(this@Notification, R.color.red))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate()
            }
        }).attachToRecyclerView(recycler_view)

        db.collection("notification")
                .get()
                .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                    var notification_counter = 0
                    if (task.isSuccessful) {
                        for (document in task.result!!) {

                            triggerQuantity[notification_counter] =
                                    document.data.getValue("productQuantity").toString()
                            triggerProductName[notification_counter] =
                                    document.data.getValue("productName").toString()
                            notification_counter++
                        }

                    } else {
                        Toast.makeText(baseContext, "Adding failed", Toast.LENGTH_SHORT).show()
                    }

                    for (i in 0 until notification_counter) {
                        notificationList.add(
                                i + 1, NotificationItem(
                                R.drawable.ic_baseline_notifications_24,
                                "Alert for " + triggerProductName[i],
                                "Trigger Alert at quantity " + triggerQuantity[i]
                        )
                        )
                    }
                    adapter.notifyItemInserted(1)

                    db.collection("products")
                            .get()
                            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                                var counter = 0
                                if (task.isSuccessful) {
                                    for (document in task.result!!) {

                                        quantity[counter] =
                                                document.data.getValue("productQuantity").toString()
                                        productName[counter] =
                                                document.data.getValue("productName").toString()
                                        counter++
                                    }

                                } else {
                                    Toast.makeText(baseContext, "Adding failed", Toast.LENGTH_SHORT).show()
                                }



                                for (noti_count in 0 until notification_counter) {
                                    for (prod_count in 0 until counter) {
                                        if (triggerProductName[noti_count] == productName[prod_count]) {
                                            if (quantity[prod_count].toInt() <= triggerQuantity[noti_count].toInt()) {
                                                when {
                                                    noti_count % 4 == 0 -> {
                                                        sendOnChannel1(
                                                                productName[prod_count] + " alert triggered",
                                                                "Current quantity " + quantity[prod_count] + " is lower than trigger quantity " + triggerQuantity[noti_count]
                                                        )
                                                    }
                                                    noti_count % 4 == 1 -> {
                                                        sendOnChannel2(
                                                                productName[prod_count] + " alert triggered",
                                                                "Current quantity " + quantity[prod_count] + " is lower than trigger quantity " + triggerQuantity[noti_count]
                                                        )
                                                    }
                                                    noti_count % 4 == 2 -> {
                                                        sendOnChannel3(
                                                                productName[prod_count] + " alert triggered",
                                                                "Current quantity " + quantity[prod_count] + " is lower than trigger quantity " + triggerQuantity[noti_count]
                                                        )
                                                    }
                                                    noti_count % 4 == 3 -> {
                                                        sendOnChannel4(
                                                                productName[prod_count] + " alert triggered",
                                                                "Current quantity " + quantity[prod_count] + " is lower than trigger quantity " + triggerQuantity[noti_count]
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            });


                });
    }

    fun sendOnChannel1(title: String, message: String){
        val title = title
        val message = message
        val notification = NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_looks_one_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()

        notificationManager.notify(1, notification)
    }
    fun sendOnChannel2(title: String, message: String){
        val title = title
        val message = message
        val notification = NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_baseline_looks_two_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()

        notificationManager.notify(2, notification)
    }
    fun sendOnChannel3(title: String, message: String){
        val title = title
        val message = message
        val notification = NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setSmallIcon(R.drawable.ic_baseline_looks_3_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()

        notificationManager.notify(3, notification)
    }
    fun sendOnChannel4(title: String, message: String){
        val title = title
        val message = message
        val notification = NotificationCompat.Builder(this, CHANNEL_4_ID)
                .setSmallIcon(R.drawable.ic_baseline_looks_4_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()

        notificationManager.notify(4, notification)
    }

    private fun generateDummyList(size: Int): ArrayList<NotificationItem> {
        val list = ArrayList<NotificationItem>()
        for (i in 0 until size) {
            val drawable = R.drawable.ic_baseline_notifications_24
            val item = NotificationItem(
                    drawable,
                    "This is the notification section",
                    "You may create alerts here"
            )
            list += item
        }

        return list

    }

    fun insertItem(view: View){
//        val notificationDialog = notification_dialog()
//        notificationDialog.show(supportFragmentManager, "example dialog")
        val dialogView = LayoutInflater.from(this).inflate(R.layout.insert_alert_layout, null)

        val myBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)

        val mAlertDialog = myBuilder.show()
        dialogView.buttonInsertNotificationCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
        dialogView.buttonInsertNotificationOk.setOnClickListener {

            db.collection("products")
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->

                        var insert_notification_counter = 0
                        if (task.isSuccessful) {
                            for (document in task.result!!) {

                                insertNotificationProductName[insert_notification_counter] =
                                        document.data.getValue("productName").toString()
                                insert_notification_counter++

                            }

                        } else {
                            Toast.makeText(baseContext, "Adding failed", Toast.LENGTH_SHORT).show()
                        }

                        val productName = dialogView.edit_product_name.text.toString()
                        val triggerQuantity = dialogView.edit_trigger_quantity.text.toString()

                        if (productName.isEmpty()) {
                            dialogView.edit_product_name.error = "Product name is required!"
                            dialogView.edit_product_name.requestFocus()
                        }

                        if (triggerQuantity.isEmpty()) {
                            dialogView.edit_trigger_quantity.error = "Price is required!"
                            dialogView.edit_trigger_quantity.requestFocus()
                        }

                        for(i in 0 until insert_notification_counter){
//                        Toast.makeText(this,  insertNotificationProductName[i], Toast.LENGTH_SHORT).show()
                            if(productName == insertNotificationProductName[i]){
                                productNameFlag = true
                            }
                        }
                        if(triggerQuantity.matches("-?\\d+(\\.\\d+)?".toRegex())){
                            quantityIntFlag = true
                        }
                        val flag =
                                productName.isNotEmpty() && triggerQuantity.isNotEmpty()

                        if(!productNameFlag){
                            Toast.makeText(this, "Wrong product name inserted, no such product exist", Toast.LENGTH_SHORT).show()
                        }
                        if(!quantityIntFlag){
                            Toast.makeText(this, "Trigger quantity must be integer only", Toast.LENGTH_SHORT).show()
                        }

                        if (flag && productNameFlag && quantityIntFlag) {
                            val note: MutableMap<String, Any> = HashMap()
                            note[KEY_PRODUCT_NAME] = productName
                            note[KEY_PRODUCT_QUANTITY] = triggerQuantity
                            db.collection("notification").document().set(note)
                                    .addOnSuccessListener {
                                        val intent = Intent(this, Notification::class.java)
                                        startActivity(intent)
                                        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                                    }
                            mAlertDialog.dismiss()
                        }
                    });
        }
    }

    fun removeItem(view: View){
        val index = 1

        notificationList.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

}