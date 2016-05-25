package app.bennsandoval.com.woodmin.interfaces;

import java.util.Map;

import app.bennsandoval.com.woodmin.models.customers.Customers;
import app.bennsandoval.com.woodmin.models.orders.Count;
import app.bennsandoval.com.woodmin.models.orders.Notes;
import app.bennsandoval.com.woodmin.models.orders.OrderResponse;
import app.bennsandoval.com.woodmin.models.orders.OrderUpdate;
import app.bennsandoval.com.woodmin.models.orders.Orders;
import app.bennsandoval.com.woodmin.models.products.Products;
import app.bennsandoval.com.woodmin.models.shop.Shop;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface Woocommerce {

    @GET("wc-api/v3/")
    Call<Shop> getShop();

    @GET("wc-api/v3/orders/count")
    Call<Count> countOrders();

    @GET("wc-api/v3/orders")
    Call<Orders> getOrders(@QueryMap Map<String, String> options);

    @GET("wc-api/v3/orders/{orderId}/notes")
    Call<Notes> getOrdersNotes(@QueryMap Map<String, String> options,
                               @Path("orderId") String orderId);

    @GET("wc-api/v3/products/count")
    Call<Count> countProducts();

    @GET("wc-api/v3/products")
    Call<Products> getProducts(@QueryMap Map<String, String> options);

    @GET("wc-api/v3/customers/count")
    Call<Count> countCustomers();

    @GET("wc-api/v3/customers")
    Call<Customers> getCustomers(@QueryMap Map<String, String> options);

    @PUT("wc-api/v3/orders/{orderId}")
    Call<OrderResponse> updateOrder(@Path("orderId") String orderId,
                                    @Body OrderUpdate order);

    @POST("wc-api/v3/orders")
    Call<OrderResponse> insertOrder(@Body OrderResponse order);

}
