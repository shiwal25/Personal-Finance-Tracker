# 📊Personal Finance Tracker

An offline-first, intuitively designed personal finance application built entirely with **Kotlin** and **Jetpack Compose**. 

This app helps users track their daily expenses, visualize their spending habits over time, and manage a monthly budget through a clean, responsive Material 3 user interface. 

## ✨ Features

* **Dashboard & Analytics:** Real-time visual insights using custom line and bar charts to display 7-day spending trends and income vs. expense breakdowns.
* **Offline-First Architecture:** All financial data is securely saved on the local device using **Room Database**, ensuring immediate load times and zero dependency on network connectivity.
* **Modern UI/UX:** Built 100% in Jetpack Compose featuring custom dialogs, empty-state handling, and smooth state-driven animations.

## 🛠️ Tech Stack & Architecture

This project was built adhering to modern Android development practices and the recommended app architecture guidelines.

* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Architecture:** MVVM (Model-View-ViewModel) + Unidirectional Data Flow (UDF)
* **Language:** [Kotlin](https://kotlinlang.org/)
* **Local Storage:** [Room SQLite](https://developer.android.com/training/data-storage/room) (Relational Data) & SharedPreferences (Key-Value)
* **Asynchronous Programming:** Kotlin Coroutines & Flow for reactive UI updates
* **Dependency Injection:** Manual
* **Charting:** Charty


## 🏗️ Architecture Highlights

The app strictly follows the **MVVM pattern** to separate business logic from UI rendering:
1. **UI Layer (Compose):** Observes state `StateFlow` emitted by the ViewModel and sends user events (clicks, inputs) back up.
2. **ViewModel Layer:** Handles the business logic (e.g., calculating budget percentages, grouping transactions by date for charts) and exposes a UI State.
3. **Data Layer (Repository/Room):** Acts as the single source of truth, abstracting the SQLite database operations and returning asynchronous data streams.
