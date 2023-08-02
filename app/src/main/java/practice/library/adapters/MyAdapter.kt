package practice.library.adapters
import android.app.Activity
import android.content.ClipData.Item
import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import practice.library.R
import practice.library.models.Book
import practice.library.retrofit.Common
class MyAdapter(
    private val activity: Activity,
    private val context: Context,
    private var booksArray: ArrayList<Book>,
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private var listener: (() -> Unit)? = null
    fun setListener(listener: (() -> Unit)?) {
        this.listener = listener
    }

    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val name: TextView = itemView.findViewById(R.id.name_book_view)
        val author: TextView = itemView.findViewById(R.id.name_book_author_view)
        val image: ImageView = itemView.findViewById(R.id.image_book_view)
        val mainLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.book_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = booksArray[position].name
        holder.author.text = String.format(
            "%s %s",
            booksArray[position].author.first_name,
            booksArray[position].author.second_name
        )
        Glide.with(activity).load(booksArray[position].photo_url).into(holder.image)
        holder.mainLayout.setOnClickListener(View.OnClickListener {
            Common.currentBook = booksArray[position]
            listener?.invoke()
        })
    }

    override fun getItemCount(): Int {
        return booksArray.size
    }


    fun setFilteredList(filteredBook: ArrayList<Book>) {
        this.booksArray = filteredBook
        notifyDataSetChanged()
    }


}