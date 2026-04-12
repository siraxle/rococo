package guru.qa.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

    @GET("/register")
    Call<ResponseBody> requestRegisterForm();

    @POST("/register")
    @FormUrlEncoded
    Call<Void> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Header("X-XSRF-TOKEN") String xsrfToken
    );

    @GET("/oauth2/authorize")
    Call<Void> authorize(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query("redirect_uri") String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codeChallengeMethod
    );

    @POST("/login")
    @FormUrlEncoded
    Call<Void> login(
            @Field("username") String username,
            @Field("password") String password,
            @Header("X-XSRF-TOKEN") String xsrfToken
    );

    @POST("/oauth2/token")
    @FormUrlEncoded
    Call<JsonNode> token(
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("client_id") String clientId,
            @Field("code_verifier") String codeVerifier,
            @Field("grant_type") String grantType
    );
}