package com.novaservices.lotonovabanklot.data.api

import android.telecom.Call
import com.novaservices.lotonovabanklot.domain.LoginResponse
import com.novaservices.lotonovabanklot.domain.Manager
import com.novaservices.lotonovabanklot.domain.Play
import com.novaservices.lotonovabanklot.domain.RequestResponse
import com.novaservices.netwalk.domain.CaseById
import com.novaservices.netwalk.domain.FinishedTicket
import com.novaservices.netwalk.domain.MerchantData
import com.novaservices.netwalk.domain.NovaWalkUser
import com.novaservices.netwalk.domain.TicketsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File


interface APIService {
//    NetWalk
@GET("ntw/get-all-cases")
suspend fun getAllTickets(): Response<TicketsResponse>

@POST("ntw/get-all-cases-by-user")
suspend fun getAllTicketsByUser(@Body NovaWalkRequest: CaseById): Response<TicketsResponse>

@POST("ntw/get-all-cases-by-afiliated")
suspend fun getAllTicketsByAfiliation(@Body NovaWalkRequest: CaseById): Response<TicketsResponse>

@POST("ntw/insert-merchant-data")
suspend fun insertUpdatedMerchantData(@Body NovaWalkRequest: MerchantData): Response<RequestResponse>

@POST("login-nova-walk")
suspend fun loginNovawalk(@Body NovaWalkRequest: NovaWalkUser): Response<LoginResponse>

@POST("register/new-novawalk-user")
suspend fun registerNovawalk(@Body NovaWalkRequest: NovaWalkUser): Response<RequestResponse>

@POST("ntw/register-result-ticket")
suspend fun postFinishedTicket(@Body ticketRequest: FinishedTicket): Response<RequestResponse>


@Multipart
@POST("image")
suspend fun uploadFile(
    @Part file: MultipartBody.Part?,
    @Part("file") name: RequestBody?,
    @Part("id") id: String,
    @Part("FileToSendBASE64") FileToSendBASE64: String
): Response<RequestResponse?>?

//    @Multipart
//    suspend fun uploadImage(
//        @Part image: MultipartBody.Part
//    )
//


//    Endpoinst NovaLoto
    @POST("ticket/registerNewTicket")
    suspend fun postRegisterTicket(@Body ticketRequest: Play): Response<RequestResponse>
    @GET("excel")
    suspend fun excel(): Response<File>

// ---------------------------------------------------------------------------------------------------------------------------



    //Register User
    @POST("register/new-user")
    suspend fun postRegisterRequest(@Body registerRequest: Manager): Response<RequestResponse>
    //Lpgin User
    @POST("login")
    suspend fun postLoginRequest(@Body loginRequest: Manager): Response<LoginResponse>
    //GET Recharge and validate funds
    @GET("rx/getByReference")
    suspend fun getByReferenceRequest(@Body registerRequest: Manager): Response<RequestResponse>
    //GET Recharge and validate funds
//    @POST("user/data")
//    suspend fun getuserDataRequest(@Body userDataRequest: Manager): Response<ResultNew>

//    @POST("user/get-waves-nova-balance")
//    suspend fun getUserWaves(@Body novaBalance: WavesRequest): Response<WavesResponse>

    //   ------------------------------------------------
    //POST New Reference
//    @POST("rx/registerNewReference")
//    suspend fun postRegisterRechargeRequest(@Body regularTransference: Transfer): Response<RequestResponse>
//
//    //  -------------------------------------------------
////    @POST("tx/new")
////    suspend fun postSendFundsRequest(@Body registerRequest: SendAsset): Response<RequestResponse>
////  -------------------------------------------------
//    @POST("rx/approve")
//    suspend fun postValidateRechargeRequest(@Body registerValidateRechargePayment: ValidateRechargePayment): Response<RequestResponse>
//
////    @POST("rx/sendDirectTransference")
////    suspend fun sendDirectTransference(@Body SendTransferRecharge: DirectTransfer): Response<RequestResponse>
//
//    @POST("tx/directTransfer")
//    suspend fun sendDirectTransference(@Body sendTransferRecharge: DirectTransfer): Response<RequestResponse>
//
//    //    Get User Received Transferences
//    @POST("tx/getAllTransactionsByUserReference")
//    suspend fun getUserTransaction(@Body userName: TransactionInvoker): Response<TransactionsData>
//    //  Get User Maded Recharges
//    @POST("rx/getAllRechargesByUserSender")
//    suspend fun getAllRechargesByUser(@Body rechargeUserName: RechargeInvoker): Response<RechargeDetails>
//    @Multipart
//    @POST("user/profile/change-photo")
//    fun changeUserProfilePhoto(
//        @Part image: MultipartBody.Part,
//    ): Response<ImageResponse>

//    @Multipart
//    @POST("uploadAttachment")
//    Call<MyResponse> uploadAttachment(@Part MultipartBody.Part filePart);



}