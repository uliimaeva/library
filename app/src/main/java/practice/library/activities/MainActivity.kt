package practice.library.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import practice.library.R
import practice.library.databinding.ActivityMainBinding
import practice.library.fragments.*
import practice.library.retrofit.Common
import practice.library.viewModels.UsersViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayDeque


class MainActivity : AppCompatActivity() {

    var mainFragment = MainFragment()
    var catalogFragment = CatalogFragment()
    var favoriteFragment = FavoriteFragment()

    lateinit var nav_image: ImageView
    lateinit var nav_name: TextView
    lateinit var nav_phone: TextView

    lateinit var searchView: SearchView

    private lateinit var binding: ActivityMainBinding
    private var selectedBottomItem: Int = R.id.Home
    private var usersViewModel = UsersViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(botListener)

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(navListener)


        mainFragment.hideFrameListener = {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                findViewById<FrameLayout>(R.id.constraint).visibility = View.GONE
        }
        mainFragment.showFrameListener = {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            findViewById<FrameLayout>(R.id.constraint).visibility = View.VISIBLE
        }

        catalogFragment.hideFrameListener = {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            findViewById<FrameLayout>(R.id.constraint).visibility = View.GONE
        }
        catalogFragment.showFrameListener = {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            findViewById<FrameLayout>(R.id.constraint).visibility = View.VISIBLE
        }

        favoriteFragment.hideFrameListener = {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            findViewById<FrameLayout>(R.id.constraint).visibility = View.GONE
        }
        favoriteFragment.showFrameListener = {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            findViewById<FrameLayout>(R.id.constraint).visibility = View.VISIBLE
        }


        catalogFragment.frameId = R.id.constraint
        mainFragment.setFrameId(R.id.constraint)
        favoriteFragment.setFrameId(R.id.constraint)
        catalogFragment.toolBarId1 = R.id.toolBarMain
        catalogFragment.toolBarId2 = R.id.toolBarBook
        mainFragment.toolBarId1 = R.id.toolBarMain
        mainFragment.toolBarId2 = R.id.toolBarBook
        favoriteFragment.toolBarId1 = R.id.toolBarMain
        favoriteFragment.toolBarId2 = R.id.toolBarBook
        supportFragmentManager.beginTransaction().replace(R.id.constraint, mainFragment).commit()

        setSupportActionBar(findViewById(R.id.toolBarMain))
        setSupportActionBar(findViewById(R.id.toolBarBook))
        binding.toolBarMain.title = "\"Библиотека\""

        setDataOnProfile()
    }

    private fun setDataOnProfile() {
        nav_image = binding.navigationView.getHeaderView(0).findViewById(R.id.profile_image)
        nav_name  = binding.navigationView.getHeaderView(0).findViewById(R.id.profile_name)
        nav_phone  = binding.navigationView.getHeaderView(0).findViewById(R.id.profile_phone)
        nav_name.text = String.format(
            "%s %s",
            Common.currentUser!!.first_name,
            Common.currentUser!!.second_name
        )
        nav_phone.text = Common.currentUser!!.login
        nav_image.setBackgroundResource(R.drawable.foto_for_mobile_user)
    }

    private val botListener = BottomNavigationView.OnNavigationItemSelectedListener {item ->
        //supportFragmentManager.beginTransaction().replace(R.id.constraint, mainFragment).commit()
        lateinit var selectedFragment: Fragment
        var searchButton: ActionMenuItemView = findViewById(R.id.search)
        findViewById<Toolbar>(R.id.toolBarMain).visibility = View.VISIBLE
        findViewById<Toolbar>(R.id.toolBarBook).visibility = View.GONE
        when (item.itemId) {
            R.id.Home -> {
                selectedBottomItem = R.id.Home
                searchButton.visibility = View.VISIBLE
                selectedFragment = mainFragment
            }
            R.id.category -> {
                selectedBottomItem = R.id.category
                searchButton.visibility = View.GONE
                selectedFragment = catalogFragment
            }
            R.id.favorites -> {
                selectedBottomItem = R.id.favorites
                searchButton.visibility = View.GONE
                selectedFragment = favoriteFragment
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.constraint, selectedFragment).commit()
        true
    }

    private val navListener = NavigationView.OnNavigationItemSelectedListener { item ->
        supportFragmentManager.beginTransaction().replace(R.id.constraint, mainFragment).commit()
        lateinit var selectedFragment: Fragment
        when (item.itemId) {
            R.id.p_Settings -> {
                startActivity(Intent (this, UpdateUserActivity::class.java))
            }
            R.id.p_ExitProfile -> {
                startActivity(Intent (this, SignInActivity::class.java))
            }
            R.id.p_AboutUs -> {
                startActivity(Intent (this, AboutUsActivity::class.java))
            }
            R.id.p_Exit -> {
                val manager = supportFragmentManager
                val myDialogFragment = MyDialogFragment()
                myDialogFragment.show(manager, "myDialog")
            }
        }
        true

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_book, menu)
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        lateinit var selectedFragment: Fragment
        when(item.itemId){
            R.id.arrow -> {
                selectedFragment = if(selectedBottomItem == R.id.Home) { mainFragment } else if (selectedBottomItem == R.id.favorites) { favoriteFragment } else { catalogFragment }
                binding.toolBarMain.visibility = View.VISIBLE
                binding.toolBarBook.visibility = View.GONE
                supportFragmentManager.beginTransaction().replace(R.id.constraint, selectedFragment).commit()
            }
            R.id.search -> {
                searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            mainFragment.filterList(newText)
                        }
                        return false
                    }
                })
            }
            R.id.favorites -> {
                usersViewModel.favouritesExists(Common.currentUser!!.id, Common.currentBook!!.id, object: Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            if (response.body()!!) {
                                usersViewModel.deleteFavourite(Common.currentUser!!.id, Common.currentBook!!.id, object: Callback<ResponseBody> {
                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        Toast.makeText(applicationContext, "Книга удалена из избранных!", Toast.LENGTH_LONG).show();
                                    }

                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Toast.makeText(applicationContext, "Не удалось связаться с API", Toast.LENGTH_LONG).show();
                                    }
                                })
                            } else {
                                usersViewModel.createFavourite(Common.currentUser!!.id, Common.currentBook!!.id, object: Callback<ResponseBody> {
                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        Toast.makeText(applicationContext, "Книга добавлена в избранные!", Toast.LENGTH_LONG).show();
                                    }

                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Toast.makeText(applicationContext, "Не удалось связаться с API", Toast.LENGTH_LONG).show();
                                    }
                                })
                            }
                        } else {
                            onFailure(call, Exception());
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Toast.makeText(applicationContext, "Не удалось связаться с API", Toast.LENGTH_LONG).show();
                    }

                })
            }
            R.id.author -> {
                var new_fragment = CatalogFragment()
                new_fragment.hideFrameListener = catalogFragment.hideFrameListener
                new_fragment.showFrameListener = catalogFragment.showFrameListener
                new_fragment.frameId = R.id.constraint
                new_fragment.toolBarId1 = R.id.toolBarMain
                new_fragment.toolBarId2 = R.id.toolBarBook
                new_fragment.selectedAuthorId = Common.currentBook!!.author.id
                new_fragment.tab = CurrentCatalogTab.Books
                findViewById<Toolbar>(R.id.toolBarMain).visibility = View.VISIBLE
                findViewById<Toolbar>(R.id.toolBarBook).visibility = View.GONE
                var searchButton: ActionMenuItemView = findViewById(R.id.search)
                searchButton.visibility = View.GONE
                supportFragmentManager.beginTransaction().replace(R.id.constraint, new_fragment).commit()
            }
        }
        return true
    }

}