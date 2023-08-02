package practice.library.viewModels

import android.util.Log
import practice.library.models.Author
import practice.library.retrofit.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthorViewModel {
    fun getAuthorWithCustomCallback(callback: Callback<MutableList<Author>>) {
        var call = Common.retrofitService.getAuthors();
        call.enqueue(callback);
    }

    fun getAuthor() {
        var call = Common.retrofitService.getAuthors();
        call.enqueue(object: Callback<MutableList<Author>> {
            override fun onResponse(
                call: Call<MutableList<Author>>,
                response: Response<MutableList<Author>>
            ) {
                if (response.isSuccessful) {
                    Common.allAuthors.postValue(response.body())
                    for (author in response.body()!!) {
                        Log.d("TEST", String.format("%s - %s", author.first_name, author.second_name))
                    }
                } else {
                    Common.allAuthors.postValue(null)
                }
            }

            override fun onFailure(call: Call<MutableList<Author>>, t: Throwable) {
                Common.allAuthors.postValue(null)
            }

        })
    }
}