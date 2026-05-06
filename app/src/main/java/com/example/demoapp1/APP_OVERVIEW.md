# DemoApp1 Navigation Overview

This document explains the implementation of the navigation flow from the Welcome screen to a WebView, and subsequently to a Native screen and Chrome Custom Tabs.

## 1. Welcome Screen to WebView
The transition starts in the `WelcomeScreen` composable. It receives a callback `onOpenWebView` which, when triggered, updates the `currentDestination` state in the main `DemoApp1App` composable.

**Code:**
```kotlin
// In MainActivity.kt - DemoApp1App
AppDestinations.HOME -> WelcomeScreen(
    modifier,
    onOpenWebView = { currentDestination = AppDestinations.WEBVIEW_DEMO }
)

// In MainActivity.kt - WelcomeScreen
Button(onClick = onOpenWebView) {
    Text("Launch Demo Web Page")
}
```

## 2. WebView to Native Screen (JavaScript Bridge)
The `WebViewScreen` uses an `AndroidView` to host a `WebView`. A `JavascriptInterface` is injected into the WebView, allowing JavaScript code inside the HTML to call native Kotlin functions.

**Code:**
```kotlin
// In MainActivity.kt - WebViewScreen
WebView(context).apply {
    settings.javaScriptEnabled = true
    addJavascriptInterface(object {
        @JavascriptInterface
        fun loadNative() {
            post { onLoadNative() } // Navigates to NATIVE_DETAIL_DEMO
        }
    }, "AndroidInterface")
    loadData(htmlContent, "text/html", "UTF-8")
}

// Inside htmlContent (JavaScript)
// <button onclick="AndroidInterface.loadNative()">load native screen</button>
```

## 3. Native Screen to Chrome Custom Tabs
The `NativeDetailScreen` uses the `androidx.browser:browser` library to launch a URL in a Chrome Custom Tab. This provides a high-performance, customizable browser experience that feels integrated with the app.

**Code:**
```kotlin
// In MainActivity.kt - NativeDetailScreen
Button(
    onClick = {
        val url = "https://wikipedia.org"
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, Uri.parse(url))
    },
    modifier = Modifier.fillMaxWidth()
) {
    Text("load custTab")
}
```

## 4. Returning to WebView
The `NativeDetailScreen` provides a "backToWebPage" button which resets the navigation state back to the WebView destination.

**Code:**
```kotlin
// In MainActivity.kt - DemoApp1App
AppDestinations.NATIVE_DETAIL_DEMO -> NativeDetailScreen(
    modifier,
    onBackToWeb = { currentDestination = AppDestinations.WEBVIEW_DEMO }
)
```

## Summary of Destinations
The `AppDestinations` enum manages the screens:
* `HOME`: Welcome Screen.
* `WEBVIEW_DEMO`: Embedded WebView with JS Bridge.
* `NATIVE_DETAIL_DEMO`: Native Compose screen with Custom Tabs trigger.

## Navigation Flow Summary

| Step | From Screen | To Screen | Mechanism | Description |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **Welcome** | **WebView** | Compose State | State variable `currentDestination` is updated on button click. |
| 2 | **WebView** | **Native Detail** | JS Bridge | JavaScript `onclick` calls Native Kotlin via `addJavascriptInterface`. |
| 3 | **Native Detail** | **Custom Tabs** | Android Intent | `CustomTabsIntent` opens an external URL (Wikipedia) within the app. |
| 4 | **Custom Tabs** | **Native Detail** | System Back | Closing the tab returns to the underlying activity screen. |
| 5 | **Native Detail** | **WebView** | Compose State | "Back" button resets `currentDestination` to `WEBVIEW_DEMO`. |
