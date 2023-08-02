package practice.library.retrofit

import androidx.lifecycle.MutableLiveData
import practice.library.models.Author
import practice.library.models.Book
import practice.library.models.User

enum class loginUserEnum {
    Ok,
    NotFound,
    WrongPassword,
    FailedRequest
}

enum class createUserEnum {
    Ok,
    FirstNameEmpty,
    SecondNameEmpty,
    ThirdNameEmpty,
    LoginEmpty,
    LoginPatternMismatch,
    LoginRepeat,
    PasswordEmpty,
    PasswordTooEasy,
    InvalidPassword,
    RoleEmpty,
    InvalidRole,
    NoReaction
}

object Common {
    private val BASE_URL = "https://library-api.mrtstg.ru/"
    val retrofitService: RetrofitService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)

    public var currentUser: User? = null;
    public var allUsers: MutableLiveData<MutableList<User>> = MutableLiveData();
    public var userLoginState: loginUserEnum? = null;
    public var userCreateState: MutableLiveData<createUserEnum> = MutableLiveData();
    public var userUpdateState: MutableLiveData<createUserEnum> = MutableLiveData();
    public var allBooks: MutableLiveData<MutableList<Book>> = MutableLiveData()
    public var currentBook: Book? = null
    public var allAuthors: MutableLiveData<MutableList<Author>> = MutableLiveData()
    public var currentBookIsFavoured: Boolean = false;

}