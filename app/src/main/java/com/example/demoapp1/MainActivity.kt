package com.example.demoapp1

import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.demoapp1.ui.theme.DemoApp1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoApp1Theme {
                DemoApp1App()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun DemoApp1App() {
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    if (!isLoggedIn) {
        LoginScreen(onLogin = { isLoggedIn = true })
    } else {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.filter { it.showInNavBar }.forEach {
                    item(
                        icon = {
                            Icon(
                                painterResource(it.icon),
                                contentDescription = it.label
                            )
                        },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                when (currentDestination) {
                    AppDestinations.HOME -> WelcomeScreen(
                        modifier,
                        onOpenWebView = { currentDestination = AppDestinations.WEBVIEW_DEMO }
                    )
                    AppDestinations.FAVORITES -> FavoritesScreen(modifier)
                    AppDestinations.PROFILE -> ProfileScreen(
                        modifier,
                        onLogout = { isLoggedIn = false },
                        onEditContact = { currentDestination = AppDestinations.EDIT_CONTACT }
                    )
                    AppDestinations.EDIT_CONTACT -> EditContactScreen(
                        modifier,
                        onBack = { currentDestination = AppDestinations.PROFILE }
                    )
                    AppDestinations.WEBVIEW_DEMO -> WebViewScreen(
                        modifier,
                        onClose = { currentDestination = AppDestinations.HOME },
                        onLoadNative = { currentDestination = AppDestinations.NATIVE_DETAIL_DEMO }
                    )
                    AppDestinations.NATIVE_DETAIL_DEMO -> NativeDetailScreen(
                        modifier,
                        onBackToWeb = { currentDestination = AppDestinations.WEBVIEW_DEMO }
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
    val showInNavBar: Boolean = true
) {
    HOME("Home", R.drawable.ic_home),
    FAVORITES("Favorites", R.drawable.ic_favorite),
    PROFILE("Profile", R.drawable.ic_account_box),
    EDIT_CONTACT("Edit Contact", R.drawable.ic_account_box, showInNavBar = false),
    WEBVIEW_DEMO("WebView Demo", R.drawable.ic_home, showInNavBar = false),
    NATIVE_DETAIL_DEMO("Native Detail", R.drawable.ic_home, showInNavBar = false),
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var username by remember { mutableStateOf("demo-user") }
    var password by remember { mutableStateOf("remember") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
    }
}

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onOpenWebView: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to DemoApp1!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("We're glad you're here.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onOpenWebView) {
            Text("Launch Demo Web Page")
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onEditContact: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("User Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onEditContact, modifier = Modifier.fillMaxWidth()) {
            Text("Edit Contact Details")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}

@Composable
fun EditContactScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var phone by remember { mutableStateOf("123-456-7890") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Edit Contact Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Save & Back")
        }
    }
}

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Favorites Screen")
    }
}

@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onLoadNative: () -> Unit
) {
    val htmlContent = """
        <html>
        <head>
            <style>
                body { font-family: sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; margin: 0; }
                button { margin: 10px; padding: 10px 20px; font-size: 16px; }
            </style>
        </head>
        <body>
            <br></br>
            <br></br>
            <br></br>
            <br></br>
            <h1> Simple Web Page </h1>
            <br></br>
            <br></br>
            <br></br>
            <br></br>
            <p style="font-size: 20px;">This is a Web View</p>
            <br></br>
            <br></br>
            <br></br>
            <button onclick="AndroidInterface.loadNative()">Load Native Screen</button>
            <button onclick="AndroidInterface.close()">Close Current WebView </button>
            <script>
            </script>
        </body>
        </html>
    """.trimIndent()

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun loadNative() {
                            post { onLoadNative() }
                        }

                        @JavascriptInterface
                        fun close() {
                            post { onClose() }
                        }
                    }, "AndroidInterface")
                    loadData(htmlContent, "text/html", "UTF-8")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun NativeDetailScreen(
    modifier: Modifier = Modifier,
    onBackToWeb: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("a native means", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val url = "https://wikipedia.org"
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(context, Uri.parse(url))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Load CutomTab - Wikipedia url")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBackToWeb, modifier = Modifier.fillMaxWidth()) {
            Text("Back to WebView")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DemoApp1AppPreview() {
    DemoApp1Theme {
        DemoApp1App()
    }
}
