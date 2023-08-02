package practice.library.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import practice.library.R
import practice.library.activities.MainActivity
import practice.library.adapters.MyAdapter
import practice.library.models.Book
import practice.library.models.User
import practice.library.retrofit.Common
import practice.library.viewModels.BookViewModel
import practice.library.viewModels.UsersViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.io.path.fileVisitor

class BookViewFragment : Fragment() {
    public var frameId: Int? = null;
    public var previous_fragment: Fragment? = null;

    lateinit var imageView: ImageView
    lateinit var name: TextView
    lateinit var author: TextView
    lateinit var amount: TextView
    lateinit var descriptor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().findViewById<Toolbar>(R.id.toolBarMain).visibility = View.GONE
        requireActivity().findViewById<Toolbar>(R.id.toolBarBook).visibility = View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(this, object:
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (frameId != null && previous_fragment != null) {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(frameId!!, previous_fragment!!)?.commit()
                }
            }
        }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_book_view, container, false)

        imageView = rootView.findViewById(R.id.book_cover)
        name = rootView.findViewById(R.id.name_book)
        author = rootView.findViewById(R.id.author_book)
        amount = rootView.findViewById(R.id.amount_book)
        descriptor = rootView.findViewById(R.id.description)
        Glide.with(requireActivity()).load(Common.currentBook!!.photo_url).into(imageView)
        name.text = Common.currentBook!!.name
        author.text = String.format(
            "%s %s",
            Common.currentBook!!.author.first_name,
            Common.currentBook!!.author.second_name
        )
        amount.text = String.format(
            "Количество: %s",
            Common.currentBook!!.remainder
        )
        descriptor.text = Common.currentBook!!.description

        return rootView
    }


}