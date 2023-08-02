package practice.library.viewModels
import android.util.Log
import androidx.lifecycle.*
import okhttp3.ResponseBody
import org.json.JSONObject
import practice.library.models.*
import practice.library.retrofit.Common
import practice.library.retrofit.createUserEnum
import practice.library.retrofit.loginUserEnum
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class UsersViewModel {

    fun favouritesExists(uid: Int, bid: Int, callback: Callback<Boolean>) {
        Common.retrofitService.favouritesExists(uid, bid).enqueue(callback);
    }

    fun getFavourites(uid: Int, callback: Callback<MutableList<Book>>) {
        var call = Common.retrofitService.getFavourites(uid);
        call.enqueue(callback);
    }

    fun createFavourite(uid: Int, bid: Int, callback: Callback<ResponseBody>) {
        var call = Common.retrofitService.createFavourites(uid, bid);
        call.enqueue(callback)
    }

    fun deleteFavourite(uid: Int, bid: Int, callback: Callback<ResponseBody>) {
        var call = Common.retrofitService.deleteFavourites(uid, bid);
        call.enqueue(callback);
    }

    fun getUsers() {
        val call = Common.retrofitService.getUsers();
        call.enqueue(object: Callback<MutableList<User>> {
            override fun onResponse(
                call: Call<MutableList<User>>,
                response: Response<MutableList<User>>
            ) {
                if (response.isSuccessful) {
                    Common.allUsers.postValue(response.body())
                    Log.d("USERS", "Users are successfully loaded")
                } else {
                    Common.allUsers.postValue(null);
                    Log.d("USERS", "Users request is unsuccessful")
                }
            }

            override fun onFailure(call: Call<MutableList<User>>, t: Throwable) {
                Common.allUsers.postValue(null);
                Log.d("USERS", String.format("Users request failed: %s", t.toString()))
            }
        })
    }

    fun createUser(data: UserCreate) {
        val call = Common.retrofitService.createUser(data);
        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Common.userCreateState.postValue(createUserEnum.Ok);
                } else {
                    try {
                        val error_obj = JSONObject(response.errorBody()?.string()!!);
                        val message: String = error_obj.getString("detail");
                        Common.userCreateState.postValue(when(message) {
                            "Password is too easy" -> createUserEnum.PasswordTooEasy
                            "Login must be phone number" -> createUserEnum.LoginPatternMismatch
                            "Password contains forbidden symbols" -> createUserEnum.InvalidPassword
                            "Login must be unique" -> createUserEnum.LoginRepeat
                            else -> {
                                Log.d("USER-CREATE", String.format("Unhandled error: %s", message));
                                null
                            }
                        })
                    } catch (e: Exception) {
                        Common.userCreateState.postValue(null);
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Common.userCreateState.postValue(null);
            }
        })
    }

    fun patchUser(uid: Int, data: UserPatch, callback: Callback<User>) {
        var call = Common.retrofitService.patchUser(uid, data);
        call.enqueue(callback);
    }
}