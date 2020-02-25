package dreamcatcher.howaboutit.network

import io.reactivex.Observable
import retrofit2.http.GET

// External gate for communication with API endpoints (operated by Retrofit)
interface ApiClient {

    @GET(Constants.OLD_DATABASE_URL)
    fun getItems(): Observable<List<ItemPojo>>

    @GET(Constants.PROTIPS_URL)
    fun getProtips(): Observable<List<ProtipPojo>>
}