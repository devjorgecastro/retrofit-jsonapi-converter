package tech.jorgecastro.jsonapi.example

import com.squareup.moshi.Json
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiRelationship
import tech.jorgecastro.jsonapi.JsonApiResource

interface OrderApi {
    @GET("v2/5e8246152f00000d002fb9e9")
    fun getOrderDetailWithRxJava(): Single<OrderDetail>


    @GET("v2/5e8246152f00000d002fb9e9")
    suspend fun getOrderDetailWithFlow(): Flow<OrderDetail>
}


@JsonApiResource(name = "orderDetail")
data class OrderDetail(
    @field:Json(name = "id") val id: String = "",
    val reference: String = "",
    val status: String = "",
    @Json(name = "store_id") val storeId: Long = 0,
    val date: OrderDate? = null,
    @Json(name = "total_products") val totalProducts: Int = 0,
    @Json(name = "total_amount") val totalAmount: Int = 0,
    @Json(name = "user_address_latitude") val userAddressLatitude: String = "",
    @Json(name = "user_address_longitude") val userAddressLongitude: String = "",
    @Json(name = "delivery_date") val deliveryDate: DeliveryDate? = null,
    @Json(name = "delivery_time") val deliveryTime: String = "",
    val subtotal: Double = 0.0,
    @Json(name = "delivery_amount") val deliveryAmount: Int,
    @Json(name = "discount_amount") val discountAmount: Int = 0,
    @Json(name = "payment_method") val paymentMethod: String = "",
    @Json(name = "management_date") val managementDate: String? = null,
    val source: String = "",
    @Json(name = "user_address") val userAddress: String = "",
    @Json(name = "store_name") val storeName: String = "",
    @Json(name = "is_express") val isExpress: Boolean = false,
    @Json(name = "credit_card_info") val creditCardInfo: String? = null,
    @Json(name = "hash_code") val hashCode: String = "",
    @JsonApiRelationship(name = "products", relationship = "products") var products: List<Product>? = listOf() // name is JsonApiResource of Product
)

data class OrderDate(
    val date: String = "",
    @Json(name = "timezone_type") val timezoneType: String = "",
    val timezone: String = ""
)

data class DeliveryDate(
    val date: String = "",
    @Json(name = "timezone_type") val timezoneType: Int = 0,
    val timezone: String = ""
)

@JsonApiResource(name = "products")
data class Product(
    @Json(name = "id") val id: String = "",
    val slug: String = "",
    @Json(name = "special_price") val specialPrice: Double? = null,
    val price:Long = 0,
    @Json(name = "store_id") val storeId: Int = 0,
    @Json(name = "discount_percentage") val discountPercentage: Double? = null,
    @Json(name = "quantity_special_price") val quantitySpecialPrice: Double? = null,
    @Json(name = "delivery_discount_amount") val deliveryDiscountAmount: Double? = null,
    @Json(name ="has_warning") val hasWarning: Boolean = false,
    val sponsored: Boolean = false,
    val pum: List<String> = listOf(),
    val volume: Int = 0,
    val weight: Int = 0
)

