<p align="center">
  <a href=""><img width="200" height="200" src="app/src/main/res/20250812_1306_Namkeen%20POS%20App%20Icon_simple_compose_01k2ek8ve6f3c9zee9j4y418xw.png"></a>
</p>

<p align="center">
<a href="https://git.io/typing-svg"><img src="https://readme-typing-svg.herokuapp.com?font=Montserrat&weight=700&size=30&pause=1000&color=66C5FF&center=true&width=435&lines=RetailEase+POS;Built+with+love+%F0%9F%92%9F" alt="Typing SVG" /></a>
</p>

<p align="center">
  <a href="https://www.android.com"><img src="https://forthebadge.com/images/badges/built-for-android.svg"></a>
  <a href="https://github.com/ronnydrooid"><img src="https://forthebadge.com/images/badges/built-with-love.svg"/></a>
</p>


<p align="center">
  <img alt="GitHub Created At" src="https://img.shields.io/github/created-at/ronnydrooid/RetailEase">
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/ronnydrooid/RetailEase">
  <img alt="GitHub repo size in bytes" src= "https://img.shields.io/github/repo-size/ronnydrooid/RetailEase">
  <a href="https://www.repostatus.org/#active"><img src="https://www.repostatus.org/badges/latest/active.svg" alt="Project Status: Active" /></a>
  <a href="https://github.com/ronnydrooid/RetailEase/releases"><img src="https://img.shields.io/github/v/release/ronnydrooid/RetailEase" alt="Current release Version" /></a>
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

👉 [Download Sample Data](https://github.com/ronnydrooid/RetailEase/releases/download/v1.0.0/demo_prefill_data_named.json)

Steps:
1. Download the JSON file above.
2. Place it in the appropriate app storage location.
3. Import this file from Import JSON option in the app settings and you are good to go.
4. Explore the app with prefilled **50 products** and **25 salesmen**.

------

<h2 align="center">Features</h2>

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

<h2 align="center">Tech Stack 🛠️</h3>
<table align="center">
  <tr>
    <td align="center"><strong>Language & Core</strong></td>
    <td align="center"><strong>UI</strong></td>
    <td align="center"><strong>Architecture & Data</strong></td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://kotlinlang.org/" target="_blank"><img src="https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white"/></a>
      <a href="https://kotlinlang.org/docs/coroutines-overview.html" target="_blank"><img src="https://img.shields.io/badge/Coroutines-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white"/></a>
      <a href="https://github.com/Kotlin/kotlinx.serialization" target="_blank"><img src="https://img.shields.io/badge/Serialization-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white"/></a>
    </td>
    <td align="center">
      <a href="https://developer.android.com/jetpack/compose" target="_blank"><img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/></a>
      <a href="https://m3.material.io/" target="_blank"><img src="https://img.shields.io/badge/Material%20Design%203-008168?style=for-the-badge&logo=materialdesign&logoColor=white"/></a>
      <a href="https://coil-kt.github.io/coil/" target="_blank"><img src="https://img.shields.io/badge/Coil%20v3-C43427?style=for-the-badge"/></a>
      <img src="https://img.shields.io/badge/Google%20Fonts-4285F4?style=for-the-badge&logo=googlefonts&logoColor=white"/>
    </td>
    <td align="center">
      <a href="https://developer.android.com/training/data-storage/room" target="_blank"><img src="https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=android&logoColor=white"/></a>
      <a href="https://developer.android.com/topic/libraries/architecture/datastore" target="_blank"><img src="https://img.shields.io/badge/DataStore-4285F4?style=for-the-badge&logo=android&logoColor=white"/></a>
      <a href="https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/" target="_blank"><img src="https://img.shields.io/badge/Flow-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white"/></a>
      <img src="https://img.shields.io/badge/ViewModel-4285F4?style=for-the-badge&logo=android&logoColor=white"/>
    </td>
  </tr>
  <tr>
    <td align="center"><strong>Navigation & DI</strong></td>
    <td align="center"><strong>Security & Testing</strong></td>
    <td align="center"><strong>Printing</strong></td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://developer.android.com/jetpack/compose/navigation" target="_blank"><img src="https://img.shields.io/badge/Navigation-4285F4?style=for-the-badge&logo=android&logoColor=white"/></a>
      <a href="https://dagger.dev/hilt/" target="_blank"><img src="https://img.shields.io/badge/Hilt-4285F4?style=for-the-badge&logo=android&logoColor=white"/></a>
    </td>
    <td align="center">
      <a href="https://developer.android.com/jetpack/androidx/releases/biometric" target="_blank"><img src="https://img.shields.io/badge/Biometric-4285F4?style=for-the-badge&logo=android&logoColor=white"/></a>
      <a href="https://junit.org/junit4/" target="_blank"><img src="https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white"/></a>
      <a href="https://developer.android.com/training/testing/espresso" target="_blank"><img src="https://img.shields.io/badge/Espresso-4285F4?style=for-the-badge&logo=android&logoColor=white"/></a>
    </td>
    <td align="center">
      <a href="https://www.bluetooth.com/" target="_blank"><img src="https://img.shields.io/badge/Bluetooth-0082FC?style=for-the-badge&logo=bluetooth&logoColor=white"/></a>
      <a href="https://www.usb.org/" target="_blank"><img src="https://img.shields.io/badge/USB-00569D?style=for-the-badge&logo=usb&logoColor=white"/></a>
    </td>
  </tr>
</table>

- Huge thanks to **[DantSu](https://github.com/DantSu)** for the excellent  
  [ESC/POS ThermalPrinter library](https://github.com/DantSu/ESCPOS-ThermalPrinter-Android) which powers receipt printing in this app.

------

<h2 align="center">Contributions</h2>

Contributions are welcome!  
However, please keep in mind that this app was originally developed for a **specific business use-case**.  
PRs and issues are appreciated if they help make the app more flexible and adaptable.

------
