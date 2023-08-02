package practice.library.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import practice.library.R
import practice.library.adapters.MyAdapter
import practice.library.models.Book
import practice.library.retrofit.Common
import practice.library.viewModels.UsersViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class FavoriteFragment : Fragment() {
    private var usersViewModel: UsersViewModel = UsersViewModel()
    private var favouritesArray: ArrayList<Book> = ArrayList();
    private lateinit var recyclerView: RecyclerView
    private var frameId: Int? = null;
    public var toolBarId1: Int? = null;
    public var toolBarId2: Int? = null;
    lateinit var adapter: MyAdapter

    var showFrameListener: (() -> Unit)? = null
    var hideFrameListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        hideFrameListener!!.invoke()
        super.onCreate(savedInstanceState)
        usersViewModel.getFavourites(Common.currentUser!!.id, object: Callback<MutableList<Book>> {
            override fun onResponse(
                call: Call<MutableList<Book>>,
                response: Response<MutableList<Book>>
            ) {
                if (response.isSuccessful) {
                    favouritesArray = ArrayList(response.body())
                    for (item in response.body()!!) {
                        println(item.name);
                    }
                    Log.d("FAVOURITES", "Got favourites!")
                    updateAdapterList()
                    showFrameListener!!.invoke()
                } else {
                    favouritesArray = ArrayList()
                }
            }

            override fun onFailure(call: Call<MutableList<Book>>, t: Throwable) {
                favouritesArray = ArrayList()
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
        val rootView = inflater.inflate(R.layout.fragment_favorite, container, false)
        recyclerView = rootView.findViewById(R.id.recycler_view)
        val gridLayoutManager = GridLayoutManager(requireContext(),2)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = gridLayoutManager

        adapter  = MyAdapter(requireActivity(), requireContext(), favouritesArray)
        adapter.setListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(frameId!!, BookViewFragment())?.commit()
            activity?.findViewById<Toolbar>(toolBarId1!!)!!.visibility = View.GONE;
            activity?.findViewById<Toolbar>(toolBarId2!!)!!.visibility = View.VISIBLE;
        }
        recyclerView.adapter = adapter
        // Inflate the layout for this fragment
        return rootView
    }

    fun setFrameId(value: Int) { frameId = value; }

    fun updateAdapterList() {
        adapter.setFilteredList(favouritesArray)
    }
}