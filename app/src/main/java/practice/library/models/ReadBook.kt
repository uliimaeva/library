package practice.library.models

data class ReadBook(
    var id: Int,
    var name: String,
    var photo_url: String,
    var quantity: Int,
    var description: String,
    var remainder: Int = -1
)
