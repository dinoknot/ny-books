package nybooks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.dmribeiro87.nybooks.R
import nybooks.data.ApiService
import nybooks.data.model.Book
import nybooks.data.response.BookBodyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BooksViewModel : ViewModel() {

    val booksLiveData: MutableLiveData<List<Book>> = MutableLiveData()
    val viewFlipperLiveData: MutableLiveData<Pair<Int, Int?>> = MutableLiveData()

    fun getBooks(){
       ApiService.service.getBooks().enqueue(object: Callback<BookBodyResponse>{
           override fun onResponse(call: Call<BookBodyResponse>, response: Response<BookBodyResponse>) {
               when {
                   response.isSuccessful -> {
                       val books: MutableList<Book> = mutableListOf()

                       response.body()?.let {bookBodyResponse ->
                           for(result in bookBodyResponse.bookResults){
                               val book = result.bookDetailsResponse[0].getBooksModel()

                               books.add(book)
                           }
                       }
                       booksLiveData.value = books
                       viewFlipperLiveData.value = Pair(VIEW_FLIPPER_BOOKS, null) 
                   }
                   response.code() == 401 -> {
                       viewFlipperLiveData.value = Pair(VIEW_FLIPPER_ERROR, R.string.books_401)
                   }
                   else -> {
                       viewFlipperLiveData.value = Pair(VIEW_FLIPPER_ERROR, R.string.books_400_generic)
                   }
               }

           }

           override fun onFailure(call: Call<BookBodyResponse>, t: Throwable) {
               viewFlipperLiveData.value = Pair(VIEW_FLIPPER_ERROR, R.string.books_500_generic)

           }
       })
   }
    companion object{
        private const val VIEW_FLIPPER_BOOKS = 1
        private const val VIEW_FLIPPER_ERROR = 2

    }

}






