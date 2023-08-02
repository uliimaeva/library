package practice.library.models

data class User(
    var id: Int,
    var first_name: String,
    var second_name: String,
    var third_name: String,
    var login: String,
    var password: String,
    var role: String,
    var photo: String
)
