package com.example.sociamedia.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAOfeZ_rE:APA91bH5NXcp05FBhPDVjIzHHSP90Q-zto6_LJVSwSpZUkIfW9WNKcCQIBcz1znsw9GEKi6IZeoTX6qih4hRFHbg2UDwiXTIFE1hZipJaQelt3auAVtMsNS2VDJjxwYT7zv9U60SdE7-"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
