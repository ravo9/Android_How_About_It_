package dreamcatcher.howaboutit.network

import com.google.gson.annotations.SerializedName
import java.util.*

// ApiResponse object and its sub-objects used for de-serializing data coming from API endpoint
data class ItemPojo(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("tags")
    val tags: List<String>,

    @SerializedName("recycling steps")
    val recyclingSteps: List<String>,

    @SerializedName("imageLink link")
    val imageLink: String)