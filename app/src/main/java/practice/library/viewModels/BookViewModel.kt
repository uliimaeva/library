package practice.library.viewModels

import android.util.Log
import practice.library.models.Book
import practice.library.retrofit.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookViewModel {
    fun getBooksWithCustomCallback(callback: Callback<MutableList<Book>>) {
        val call = Common.retrofitService.getBooks();
        call.enqueue(callback);
    }

    fun getBooks() {
        var call = Common.retrofitService.getBooks();
        call.enqueue(object: Callback<MutableList<Book>> {
            override fun onResponse(
                call: Call<MutableList<Book>>,
                response: Response<MutableList<Book>>
            ) {
                if (response.isSuccessful) {
                    Common.allBooks.postValue(response.body())
                } else {
                    Common.allBooks.postValue(null)
                }
            }

            override fun onFailure(call: Call<MutableList<Book>>, t: Throwable) {
                Common.allBooks.postValue(null)
            }

        })
    }
}