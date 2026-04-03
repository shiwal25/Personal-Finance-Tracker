package com.example.personalfinancetracker

import androidx.annotation.StringRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.personalfinancetracker.ui.AddTransactionScreen
import com.example.personalfinancetracker.ui.DashBoardViewModel
import com.example.personalfinancetracker.ui.DashBoardViewModelFactory
import com.example.personalfinancetracker.ui.HomeScreen
import com.example.personalfinancetracker.ui.TransactionViewModel
import com.example.personalfinancetracker.ui.TransactionViewModelFactory


enum class PersonalFinanceTrackerScreen(@StringRes val title:Int){
    Home(title = R.string.app_name),
    Transaction(title = R.string.transaction_page),
    Insights(title = R.string.insights_page),
    AddTransaction(title = R.string.add_transaction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalFinanceTrackerAppBar(
    currentScreen: PersonalFinanceTrackerScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
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
fun FinanceBottomBar(
    navController: NavHostController,
    currentScreen: PersonalFinanceTrackerScreen
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == PersonalFinanceTrackerScreen.Home,
            onClick = {
                navController.navigate(PersonalFinanceTrackerScreen.Home.name) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentScreen == PersonalFinanceTrackerScreen.Transaction,
            onClick = {
                navController.navigate(PersonalFinanceTrackerScreen.Transaction.name) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.List, contentDescription = "Transactions") },
            label = { Text("Transactions") }
        )

        NavigationBarItem(
            selected = currentScreen == PersonalFinanceTrackerScreen.Insights,
            onClick = {
                navController.navigate(PersonalFinanceTrackerScreen.Insights.name) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Insights") },
            label = { Text("Insights") }
        )
    }
}

@Composable
fun PersonalFinanceTrackerApp(
//    transactionViewModel: TransactionViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val app = context.applicationContext as PersonalFinanceTrackerApplication

    val transactionRepository = app.container.transactionRepository

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(transactionRepository)
    )

    val dashBoardViewModel: DashBoardViewModel = viewModel(
        factory = DashBoardViewModelFactory(transactionRepository)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = PersonalFinanceTrackerScreen.valueOf(
        backStackEntry?.destination?.route ?: PersonalFinanceTrackerScreen.Home.name
    )

    Scaffold(
        topBar = {
            if (currentScreen != PersonalFinanceTrackerScreen.AddTransaction) {
                PersonalFinanceTrackerAppBar(
                    canNavigateBack = navController.previousBackStackEntry != null,
                    currentScreen = currentScreen,
                    navigateUp = { navController.navigateUp() },
                )
            }
        },
        bottomBar = {
            if (currentScreen != PersonalFinanceTrackerScreen.AddTransaction) {
                FinanceBottomBar(
                    navController = navController,
                    currentScreen = currentScreen
                )
            }
        },
        floatingActionButton = {
            if (currentScreen != PersonalFinanceTrackerScreen.AddTransaction) {
                FloatingActionButton(
                    onClick = { navController.navigate(PersonalFinanceTrackerScreen.AddTransaction.name) },
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ){ innerPadding ->
        val transactionUiState by transactionViewModel.uiState.collectAsState()
        val dashboardUiState by dashBoardViewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = PersonalFinanceTrackerScreen.Home.name,
        ) {
            composable (route = PersonalFinanceTrackerScreen.Home.name) {
                HomeScreen(
                    balance = dashboardUiState.netBalance,
                    income = dashboardUiState.totalIncome,
                    expense = dashboardUiState.totalExpense,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable (route = PersonalFinanceTrackerScreen.Transaction.name){
//                TransactionScreen()
            }
            composable (route = PersonalFinanceTrackerScreen.Insights.name){

            }
            composable(route = PersonalFinanceTrackerScreen.AddTransaction.name,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 150)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 150)
                    )
                }
            ) {
                AddTransactionScreen(
                    uiState = transactionUiState,
                    onValueChange = { newState ->
                        transactionViewModel.updateState(newState)
                    },
                    onSave = {
                        val isSaved = transactionViewModel.saveTransaction()
                        if(isSaved){
                            navController.popBackStack()
                        }
                    },
                    onCancel = {
                        transactionViewModel.resetState()
                        navController.popBackStack()
                    },
                    onNumberClick = { transactionViewModel.onNumberClick(it) },
                    onClearError = { transactionViewModel.clearError() },
                )
            }
        }
    }
}