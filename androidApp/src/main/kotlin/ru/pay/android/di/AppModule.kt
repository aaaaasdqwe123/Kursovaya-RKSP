package ru.pay.android.di
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.pay.domain.*
import ru.pay.android.data.RemoteRepository
@Module @InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun client()=HttpClient(OkHttp) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys=true }) }
        install(HttpTimeout) { requestTimeoutMillis=15000; connectTimeoutMillis=10000; socketTimeoutMillis=15000 }
    }
    @Provides @Singleton fun repository(remote: RemoteRepository): PaymentRepository=remote
    @Provides fun send(repository: PaymentRepository)=SendPayment(repository)
    @Provides fun search(repository: PaymentRepository)=SearchRecipients(repository)
}
