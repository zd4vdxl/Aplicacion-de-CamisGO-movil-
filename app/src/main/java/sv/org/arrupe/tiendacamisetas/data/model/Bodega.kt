package sv.org.arrupe.tiendacamisetas.data.model

import com.google.gson.annotations.SerializedName

data class BodegaItem(
    @SerializedName(value = "BodegaID", alternate = ["bodegaID", "bodegaId", "id"])
    val bodegaId: Int = 0,

    @SerializedName(value = "CamisetaID", alternate = ["camisetaID", "camisetaId"])
    val camisetaId: Int = 0,

    @SerializedName(value = "Talla", alternate = ["talla"])
    val talla: String = "",

    @SerializedName(value = "Stock", alternate = ["stock"])
    val stock: Int = 0
)