package co.ke.daletsys.azyma.control;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    String BASE_URL = "https://azyma.daletsys.co.ke/api/uploads/";

    @Multipart
    @POST("oUpload.php")
    Call<ResponseBody> uploadMultiple(
            @Part("description") RequestBody description,
            @Part("size") RequestBody size,
            @Part("serial") RequestBody serial,
            @Part List<MultipartBody.Part> files);
}
