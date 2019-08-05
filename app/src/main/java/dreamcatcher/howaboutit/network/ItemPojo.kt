package dreamcatcher.howaboutit.network

import com.google.gson.annotations.SerializedName

// ApiResponse object used for de-serializing data coming from API endpoint
data class ItemPojo(

    @SerializedName("ID")
    val id: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("Tags")
    val tags: String,

    @SerializedName("Image Link")
    val imageLink: String,

    @SerializedName("Bin Type")
    val binType: String,

    @SerializedName("Additional Information")
    val additionalInformation: List<String>?)