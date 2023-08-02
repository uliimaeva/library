package practice.library.retrofit

import okhttp3.ResponseBody
import practice.library.models.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("users/")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun getUsers(): Call<MutableList<User>>

    @POST("users/")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun createUser(@Body data: UserCreate): Call<User>

    @GET("books/")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun getBooks(): Call<MutableList<Book>>

    @PATCH("users/{uid}")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun patchUser(@Path("uid") uid: Int, @Body data: UserPatch): Call<User>

    @GET("authors/")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun getAuthors(): Call<MutableList<Author>>

    @GET("favourites/{uid}")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun getFavourites(@Path("uid") uid: Int): Call<MutableList<Book>>

    @DELETE("favourites/{uid}/{bid}")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun deleteFavourites(@Path("uid") uid: Int, @Path("bid") bid: Int): Call<ResponseBody>;

    @POST("favourites/{uid}/{bid}")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun createFavourites(@Path("uid") uid: Int, @Path("bid") bid: Int): Call<ResponseBody>;

    @GET("favourites/{uid}/{bid}")
    @Headers(
        "Accept:application/json",
        "Content-Type:application/json",
        "token: uli123"
    )
    fun favouritesExists(@Path("uid") uid: Int, @Path("bid") bid: Int): Call<Boolean>;
}