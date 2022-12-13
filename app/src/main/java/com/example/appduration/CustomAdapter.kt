
import AddAllAppsonScreenPoging2.Companion.Applist
import AddAllAppsonScreenPoging2.Companion.InsertContentToViews
import SaveAndloadApplistFile.Companion.writeToFile
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appduration.ItemsViewModel
import com.example.appduration.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {//https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.showappblock, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { // acties voor knop bij app (het gedrag)

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        if(Applist.get(ItemsViewModel.i).Blocked){
            holder.Button.text = "unBlock"
            holder.Button.setTextColor(Color.parseColor("#fc031c"))
        }else{
            holder.Button.text = "Block"
            holder.Button.setTextColor(Color.WHITE)
        }
        holder.Button.setOnClickListener {
            if(holder.Button.text == "Block"){
                holder.Button.text = "unBlock"
                holder.Button.setTextColor(Color.parseColor("#fc031c"))
                ItemsViewModel.Applist.get(ItemsViewModel.i).Blocked = true;
                writeToFile(ItemsViewModel.applicationContext, Applist, ItemsViewModel.packageManager);
            }else{
                holder.Button.text = "Block"
                holder.Button.setTextColor(Color.WHITE)
                ItemsViewModel.Applist.get(ItemsViewModel.i).Blocked = false;
                writeToFile(ItemsViewModel.applicationContext, Applist, ItemsViewModel.packageManager);
            }
            test(MainScope(), ItemsViewModel)//https://stackoverflow.com/questions/57770131/create-async-function-in-kotlin
        }
        holder.textView.text = ItemsViewModel.text
        holder.imageView.setImageDrawable(ItemsViewModel.d)
    }
    fun test(scope: CoroutineScope, ItemsViewModel: ItemsViewModel): Deferred<Unit> = scope.async {
        InsertContentToViews(
            ItemsViewModel.packageManager,
            ItemsViewModel.applicationContext,
            ItemsViewModel.recyclerview,
            ItemsViewModel.progressBar,
            ItemsViewModel.UsageStatsManager,
            ItemsViewModel.binding
        );
        return@async
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iconapp)
        val textView: TextView = itemView.findViewById(R.id.txtappnameRestrickted)
        val Button: Button = itemView.findViewById(R.id.btnrestricktedApp)
    }
}