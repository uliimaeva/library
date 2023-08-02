package practice.library.models

data class UserCreate(
    var first_name: String,
    var second_name: String,
    var third_name: String,
    var login: String,
    var password: String,
    val role: String = "читатель",
    val photo: String = "null"
)
