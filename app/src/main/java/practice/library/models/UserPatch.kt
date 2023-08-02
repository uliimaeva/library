package practice.library.models

data class UserPatch(
    val first_name: String? = null,
    val second_name: String? = null,
    val third_name: String? = null,
    val login: String? = null,
    val password: String? = null
)
