<p align="center">
  <a href=""><img width="200" height="200" src="app/src/main/res/20250812_1306_Namkeen%20POS%20App%20Icon_simple_compose_01k2ek8ve6f3c9zee9j4y418xw.png"></a>
</p>

<h1 align="center">RetailEase POS</h1>

<p align="center">
  <a href="https://www.android.com"><img src="https://forthebadge.com/images/badges/built-for-android.svg"></a>
  <a href="https://github.com/ronnydrooid"><img src="https://forthebadge.com/images/badges/built-with-love.svg"/></a>
</p>


<p align="center">
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/ronnydrooid/RetailEase">
  <a href="https://www.repostatus.org/#active"><img src="https://www.repostatus.org/badges/latest/active.svg" alt="Project Status: Active" /></a>
</p>

------

**RetailEase POS** is a feature-rich Point of Sale (POS) billing application for Android, built with
[Kotlin](https://kotlinlang.org/) and [Jetpack Compose](https://developer.android.com/jetpack/compose).
It is designed to streamline sales, manage wholesale/retail operations, and simplify bookkeeping
for small to medium-sized businesses.

⚠️ **Note**:  
This software was created as a private freelance project for a specific company.  
The configuration, database schema, and workflows are industry-specific, and not every feature may
be directly usable without customization. If you are testing the app, please use the provided
**sample JSON data** to prefill the app and explore its functionality.

------

<h2 align="center">Demo Prefill Data</h2>

To avoid heavy manual configuration, you can quickly set up demo data using the provided JSON file:

<a href="https://raw.githubusercontent.com/ronnydrooid/RetailEase/refs/heads/main/demo_prefill_data_named.json" download>📥 Download Sample Data</a>


Steps:
1. Download the JSON file above.
2. Place it in the appropriate app storage location (see app instructions).
3. Import this file from the app's backup/restore option.
4. Explore the app with prefilled **50 products** and **25 salesmen**.

------

<h2 align="center">Highlights</h2>

- Supports **retail and wholesale** billing.
- **Salesman-specific** pricing and discounts.
- Integrated **khatabook-style ledger system** for tracking credits/debits.
- Easy **order management** with multiple sales modes.
- Built-in **reporting system** (sales, customers, employees).
- Fully offline, no internet required.
- Admin-only access to sensitive features (customer & employee management).
- Built using modern Android development practices.

------

<h2 align="center">Screenshots</h2>

📌 *Screenshots will be added here later.*

------

<h2 align="center">Tech Stack</h2>

- **Language & Core**
    - [Kotlin 2.0.21](https://kotlinlang.org/) - Modern language for Android.
    - [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for async programming.
    - [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON backup/restore.

- **UI**
    - [Jetpack Compose](https://developer.android.com/jetpack/compose) - Native UI toolkit.
    - [Material 3](https://m3.material.io/) - Modern Material Design components.
    - [Google Fonts Compose](https://developer.android.com/jetpack/compose/text#fonts) for typography.
    - [Coil v3](https://coil-kt.github.io/coil/compose) - Image loading.
    - [Material Icons Extended](https://developer.android.com/develop/ui/compose/material/icons).

- **Architecture & Data**
    - [Room 2.7.1](https://developer.android.com/jetpack/androidx/releases/room) - Database persistence.
    - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Preferences storage.
    - [Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - Reactive data streams.
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) & Lifecycle components.

- **Navigation & Dependency Injection**
    - [Navigation-Compose 2.9.0](https://developer.android.com/jetpack/compose/navigation).
    - [Hilt 2.56.2](https://dagger.dev/hilt) - Dependency injection.
    - [Hilt Navigation Compose](https://developer.android.com/training/dependency-injection/hilt-jetpack).

- **Security**
    - [AndroidX Biometric](https://developer.android.com/jetpack/androidx/releases/biometric) - Biometric authentication.

- **Testing**
    - [JUnit 4.13.2](https://junit.org/junit4/) & AndroidX Test (JUnit + Espresso).
    - [Compose UI Test](https://developer.android.com/jetpack/compose/testing).

- **Printing**
    - [DantSu ESC/POS](https://github.com/DantSu/ESCPOS-ThermalPrinter-Android) - Thermal printer integration (Bluetooth/USB).

------

<h2 align="center">Special Thanks</h2>

- Huge thanks to **[DantSu](https://github.com/DantSu)** for the excellent  
  [ESC/POS ThermalPrinter library](https://github.com/DantSu/ESCPOS-ThermalPrinter-Android) which powers receipt printing in this app.

------

<h2 align="center">Contributions</h2>

Contributions are welcome!  
However, please keep in mind that this app was originally developed for a **specific business use-case**.  
PRs and issues are appreciated if they help make the app more flexible and adaptable.

------
