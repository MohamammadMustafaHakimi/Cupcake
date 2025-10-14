/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.content.Context
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cupcake.ui.OrderViewModel
import com.example.cupcake.ui.theme.CupcakeTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cupcake.data.DataSource
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.StartOrderScreen
import javax.security.auth.Subject
import kotlin.String
import android.content.Intent
import androidx.annotation.StringRes
import androidx.navigation.compose.currentBackStackEntryAsState

// enum class for defining the routes
enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.choose_flavor),
    Pickup(title = R.string.choose_flavor),
    Summary(title = R.string.order_summary)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(), // instance of OrdeViewModel holding UI state and business logic
    navController: NavHostController = rememberNavController() // navigation controller to manage screen navigation, defaulted to rememberNavController() for Compose navigation
) {
    val backStackEntry by navController.currentBackStackEntryAsState() // retrieves the current navigation back stack entry as a Compose State
    val currentScreen = CupcakeScreen.valueOf( // reads the route of the current destination from the navigation back stack entry
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name // if it's null (e.g at start), defaults to the Start screen route; uses the enum CupcakeScreen.valueOf to convert the route string into a typed enum for safer usage
    )
     fun cancelOrderAndNavigateToStart(
        viewModel: OrderViewModel,
        navController: NavHostController
    ) {
        viewModel.resetOrder()
        navController.popBackStack(CupcakeScreen.Start.name, inclusive = false) // pops the naviagtion back stack to the Start screen. without removing the Start screen itself; inclusive = false, means that the Start screen should not be poped off
    }

     fun shareOrder(context: Context, subject: String, summary: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, summary)
        }
         val chooser = Intent.createChooser(intent, context.getString(R.string.new_cupcake_order))
         context.startActivity(chooser)
    }


    Scaffold( // provides a common UI structure with a top app bar
        topBar = {
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null, // check if there is a previous back stack entry
                navigateUp = { navController.navigateUp() } // handles back button navigation
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState() // observes the current UI state from the viewmodel as a compose state, triggering recompostion on charge
        NavHost(
            navController = navController, // navController: Controls navigation state and actions.
            startDestination = CupcakeScreen.Start.name, // the route name of the initial screen shown, here it's Start
            modifier = Modifier.padding(innerPadding)
        ) {
            /* the content */
            composable(route = CupcakeScreen.Start.name) { // the .name is an inhereted property of ENUM classes
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }

            composable(route = CupcakeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal =  uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options =  DataSource.flavors.map { id -> context.resources.getString(id)},
                    onSelectionChanged =  { viewModel.setFlavor(it) },
                    modifier =  Modifier.fillMaxHeight()
                )
            }

            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name)},
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(route = CupcakeScreen.Summary.name) {
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
//                    onSendButtonClicked = { subject: String, summary: String ->
//
//                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CupcakeAppPreview() {
    CupcakeTheme {
        CupcakeApp()
    }
}