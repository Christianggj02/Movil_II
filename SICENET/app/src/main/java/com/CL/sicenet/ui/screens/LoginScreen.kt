@file:OptIn(ExperimentalMaterial3Api::class)

package com.CL.sicenet.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoginForm (
    vm: LoginViewModel,
    navController: NavHostController
){
    val coroutineScope = rememberCoroutineScope()
    var show: Boolean? by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                TextField(
                    modifier = Modifier.padding(16.dp),
                    label = {Text(text = "No. Control")},
                    singleLine = true,
                    value = vm.loginState.user.matricula,
                    onValueChange = {vm.updateLoginState(vm.loginState.copy(user = vm.loginState.user.copy(matricula = it)))},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Matricula"
                        )
                    }
                )
                TextField(
                    modifier = Modifier.padding(16.dp),
                    label = {Text(text = "Contraseña")},
                    singleLine = true,
                    value = vm.loginState.user.contrasenia,
                    onValueChange = {vm.updateLoginState(vm.loginState.copy(user = vm.loginState.user.copy(contrasenia = it)))},
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Contraseña"
                        )
                    }
                )

                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        coroutineScope.launch {
                            vm.login(vm.loginState.user)
                            Log.d("Resultado",vm.loginState.user.acceso.toString())
                            if (vm.loginState.user.acceso){
                                navController.navigate("home")
                                delay(500)
                                vm.updateLoginState(vm.loginState.copy(user = vm.loginState.user.copy(acceso = false)))
                            }
                        }
                    }
                ){
                    Text(text = "Iniciar sesion")
                }
            }
        }
    }

    if (vm.loginState.user.acceso){
        AlertDialog(
            text = { Text(text = "¡Bienvenido! \n ${vm.loginState.student.nombre}")},
            onDismissRequest = { /*TODO*/ },
            confirmButton = {  }
        )
    }
}