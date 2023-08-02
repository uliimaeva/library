package practice.library.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import practice.library.R
import practice.library.models.Author
import practice.library.models.Book
import practice.library.retrofit.Common
import practice.library.viewModels.AuthorViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

enum class CurrentCatalogTab {
    Authors,
    Books
}

class CatalogFragment : Fragment() {
    public var frameId: Int = 0;
    public var toolBarId1: Int = 0;
    public var toolBarId2: Int = 0;

    private var viewModel: AuthorViewModel = AuthorViewModel()
    private var authorsArray: ArrayList<Author> = ArrayList()

    var selectedAuthorId: Int = 0;
    // for adapter
    private var adapterArray: ArrayList<String> = ArrayList()
    // for authors
    private var authorsListArray: ArrayList<String> = ArrayList()
    // for books
    private var booksListArray: ArrayList<String> = ArrayList()

    var tab: CurrentCatalogTab = CurrentCatalogTab.Authors;
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var listView: ListView

    var showFrameListener: (() -> Unit)? = null
    var hideFrameListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideFrameListener!!.invoke()

        requireActivity().onBackPressedDispatcher.addCallback(this, object:
            OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (tab == CurrentCatalogTab.Books) {
                        tab = CurrentCatalogTab.Authors;
                        setList()
                    } else {
                        ActivityCompat.finishAffinity(requireActivity())
                        exitProcess(0)
                    }
                }
            }
        )

        viewModel.getAuthorWithCustomCallback(object: Callback<MutableList<Author>> {
            override fun onResponse(
                call: Call<MutableList<Author>>,
                response: Response<MutableList<Author>>
            ) {
                if (response.isSuccessful) {
                    authorsArray = ArrayList(response.body()!!);
                    Log.d("AUTHORS", "Got authors!")
                    for (author in authorsArray) {
                        authorsListArray.add(String.format("%s %s", author.first_name, author.second_name))
                    }
                    setList()
                } else {
                    authorsArray = ArrayList();
                }
            }

            override fun onFailure(call: Call<MutableList<Author>>, t: Throwable) {
                authorsArray = ArrayList();
            }
        })
        adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, adapterArray)
    }

    private fun setList() {
        adapterArray.clear()
        if (tab == CurrentCatalogTab.Authors) {
            adapterArray.addAll(authorsListArray)
        } else {
            for (book in Common.allBooks.value!!) {
                if (book.author.id == selectedAuthorId) {
                    adapterArray.add(book.name);
                }
            }
        }
        Log.d("CATALOG", String.format("Set list of %d arguments", adapterArray.size));
        adapter.notifyDataSetChanged()
        showFrameListener!!.invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_catalog, container, false)
        listView = rootView.findViewById(R.id.list_author_view)
        setList()
        listView.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (tab == CurrentCatalogTab.Authors) {
                    selectedAuthorId = authorsArray[position].id;
                    Log.d("CATALOG", String.format("Selected %s", authorsArray[position].first_name))
                    tab = CurrentCatalogTab.Books;
                    hideFrameListener!!.invoke()
                    setList()
                } else if (tab == CurrentCatalogTab.Books) {
                    var book: Book? = null;
                    for (book_obj in Common.allBooks.value!!) {
                        if (book_obj.name == adapterArray[position]) {
                            book = book_obj
                            break
                        }
                    }
                    Common.currentBook = book
                    var fragment = BookViewFragment()
                    var c_fragment = CatalogFragment()
                    c_fragment.showFrameListener = showFrameListener
                    c_fragment.hideFrameListener = hideFrameListener
                    c_fragment.frameId = frameId
                    c_fragment.toolBarId1 = toolBarId1
                    c_fragment.toolBarId2 = toolBarId2
                    fragment.frameId = frameId
                    fragment.previous_fragment = c_fragment
                    activity?.supportFragmentManager?.beginTransaction()?.replace(frameId, fragment)?.commit()
                }
            }
        })
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
        return rootView
    }

}