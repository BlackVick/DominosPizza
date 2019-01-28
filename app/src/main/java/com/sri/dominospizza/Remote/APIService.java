package com.sri.dominospizza.Remote;

import com.sri.dominospizza.Model.MyResponse;
import com.sri.dominospizza.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public interface APIService {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAGc15wMA:APA91bGxnt7aTPCNltf_WTZQdnRQaZhQXZvzXnqket3FTd-ULJPobE1Ijl0_qR16XlSRr-O32eWXprR_yKLkRxoHsTQD1CsuXEuVJAJWsmhvfKM2rAGsBHueR5muNehGzAnyH66yDcsV"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
