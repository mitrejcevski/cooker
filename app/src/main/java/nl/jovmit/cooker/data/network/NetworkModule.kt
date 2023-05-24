package nl.jovmit.cooker.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val API_ACCESS_KEY = "f605e50a2b704b6dad8087ba3846b385"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addAuthInterceptor()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipesApi(retrofit: Retrofit): RecipesApi {
        return retrofit.create(RecipesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private fun OkHttpClient.Builder.addAuthInterceptor(): OkHttpClient.Builder {
        addInterceptor { chain ->
            val url = chain.request().url
            val newUrl = url.newBuilder()
                .addQueryParameter("apiKey", API_ACCESS_KEY)
                .build()
            val updatedRequest = chain.request().newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(updatedRequest)
        }
        return this
    }
}