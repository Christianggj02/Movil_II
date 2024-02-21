package com.CL.sicenet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.CL.sicenet.ui.screens.Home
import com.CL.sicenet.ui.screens.LoginForm
import com.CL.sicenet.ui.screens.LoginViewModel
import com.CL.sicenet.ui.theme.SICENETTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SICENETTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
                    // Configura el NavHost
                    NavHost(navController, startDestination = "login") {
                        // Pantalla de inicio de sesión
                        composable("login") {
                            LoginForm(vm = loginViewModel, navController)
                        }

                        // Pantalla principal
                        composable("home") {
                            Home(vm = loginViewModel, navController)
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SICENETTheme {
        Greeting("Android")
    }
}