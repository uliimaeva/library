package practice.library.models

data class Book(
    var id: Int,
    var author: BookAuthor,
    var remainder: Int,
    var name: String,
    var photo_url: String,
    var quantity: Int,
    var description: String
)
