package com.noxy.assistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.noxy.assistant.network.NoxyApi
import com.noxy.assistant.ui.profile.NoxyProfileScreen
import com.noxy.assistant.ui.profile.NoxyProfileViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private const val PROFILE_ACTION = "PROFILE"
private const val BASE_URL = "http://10.0.2.2:8000"

class MainActivity : ComponentActivity() {

    private val api: NoxyApi by lazy { NoxyApiProvider.api }
    private val profileViewModel: NoxyProfileViewModel by lazy { NoxyProfileViewModel(api) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val action = intent?.action
            if (action == PROFILE_ACTION) {
                NoxyProfileScreen(viewModel = profileViewModel)
            } else {
                HomeScreen(onNavigateProfile = {
                    startActivity(
                        Intent(this, MainActivity::class.java).apply { action = PROFILE_ACTION }
                    )
                })
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigateProfile: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Noxy Assistant", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Bienvenue !")
            Button(onClick = onNavigateProfile) {
                Text(text = "Voir le profil Noxy")
            }
        }
    }
}

object NoxyApiProvider {
    private val json = Json { ignoreUnknownKeys = true }

    val api: NoxyApi by lazy {
        val contentType = "application/json".toMediaType()
        val client = OkHttpClient.Builder().build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()
            .create(NoxyApi::class.java)
    }
}
