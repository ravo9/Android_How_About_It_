package dreamcatcher.howaboutit.network

import com.google.gson.annotations.SerializedName
import java.util.*

// ApiResponse object and its sub-objects used for de-serializing data coming from API endpoint
data class ItemPojo(

    @SerializedName("ID")
    val id: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("Tags")
    val tags: List<String>,

    @SerializedName("Recycling Steps")
    val recyclingSteps: List<String>,

    @SerializedName("Image Link")
    val imageLink: String)