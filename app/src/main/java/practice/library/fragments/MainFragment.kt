package practice.library.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import practice.library.R
import practice.library.activities.MainActivity
import practice.library.adapters.MyAdapter
import practice.library.models.Book
import practice.library.retrofit.Common
import practice.library.viewModels.BookViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class MainFragment : Fragment() {
    private var viewModel: BookViewModel = BookViewModel()
    private var booksArray: ArrayList<Book> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var frameId: Int? = null;
    public var toolBarId1: Int? = null;
    public var toolBarId2: Int? = null;
    lateinit var adapter: MyAdapter

    var showFrameListener: (() -> Unit)? = null
    var hideFrameListener: (() -> Unit)? = null

    fun setFrameId(value: Int) { frameId = value; }

    override fun onCreate(savedInstanceState: Bundle?) {
        hideFrameListener!!.invoke()
        super.onCreate(savedInstanceState)
        viewModel.getBooksWithCustomCallback(object: Callback<MutableList<Book>>{
            override fun onResponse(
                call: Call<MutableList<Book>>,
                response: Response<MutableList<Book>>
            ) {
                if (response.isSuccessful) {
                    Common.allBooks.postValue(response.body())
                    booksArray = ArrayList(response.body()!!)
                    updateAdapterList()
                } else {
                    Common.allBooks.postValue(null);
                    booksArray = ArrayList();
                }
            }

            override fun onFailure(call: Call<MutableList<Book>>, t: Throwable) {
                Common.allBooks.postValue(null);
                booksArray = ArrayList()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this, object:
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ActivityCompat.finishAffinity(requireActivity())
                exitProcess(0)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        recyclerView = rootView.findViewById(R.id.main_recycler_123)
        val gridLayoutManager = GridLayoutManager(requireContext(),2)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager

        adapter  = MyAdapter(requireActivity(), requireContext(), booksArray)
        adapter.setListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(frameId!!, BookViewFragment())?.commit()
            activity?.findViewById<Toolbar>(toolBarId1!!)!!.visibility = View.GONE;
            activity?.findViewById<Toolbar>(toolBarId2!!)!!.visibility = View.VISIBLE;
        }
        recyclerView.adapter = adapter
        // Inflate the layout for this fragment
        return rootView
    }

    fun updateAdapterList() {
        adapter.setFilteredList(booksArray)
        showFrameListener!!.invoke()
    }

    fun filterList(text: String) {
        var filteredBooks: ArrayList<Book> = ArrayList()
        for (item in booksArray) {
            Log.d("SEARCH", String.format("Comparing %s and %s", item.name, text))
            if (item.name.contains(text, ignoreCase = true)) {
                Log.d("SEARCH", String.format("Matched %s", item.name));
                filteredBooks.add(item)
            }
        }
        if (filteredBooks.isEmpty()) {
            Toast.makeText(requireContext(), "Книг не найдено!", Toast.LENGTH_SHORT).show()
        }
        adapter.setFilteredList(filteredBooks)
    }

}