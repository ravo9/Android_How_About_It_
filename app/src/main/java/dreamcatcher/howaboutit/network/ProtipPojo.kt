package dreamcatcher.howaboutit.network

import com.google.gson.annotations.SerializedName

// ApiResponse object used for de-serializing data coming from API endpoint
data class ProtipPojo(

    @SerializedName("id")
    val id: String,

    @SerializedName("protip_text")
    val protipText: String)