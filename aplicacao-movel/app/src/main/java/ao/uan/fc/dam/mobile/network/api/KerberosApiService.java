package ao.uan.fc.dam.mobile.network.api;

import ao.uan.fc.dam.mobile.network.dto.AuthenticatorRequest;
import ao.uan.fc.dam.mobile.network.dto.AuthenticatorResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface KerberosApiService {

    @POST("/api/kerberos/create-authenticator")
    Call<AuthenticatorResponse> createAuthenticator(@Body AuthenticatorRequest request);
}
