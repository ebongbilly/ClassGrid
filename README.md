# ClassGrid

**ClassGrid** is an open-source, fully offline desktop student management system designed to simplify classroom administration and academic tracking. Built with **Kotlin** and **Compose Multiplatform**, it provides a modern, fast, and secure user experience directly on your desktop without requiring any internet connection.

## 🚀 Key Features

- **Student Registration & Class Management**: Easily record, organize, and manage student profiles categorized by their respective classes.
- **Automated Student ID Generation**: Systematically generate unique student identification numbers upon registration.
- **Exam Marks & Report Cards**:
  - Record individual exam marks per student per session.
  - Generate comprehensive, print-ready student report cards.
- **Exam Session Statistics & Analytics**:
  - Automatically generate detailed exam statistical sheets for each session.
  - Analyze academic performance at a granular level (**per class**) or an aggregate level (**globally**).

## 🛠️ Built With

- **Kotlin**: The core language for robust and expressive business logic.
- **Compose Multiplatform**: For crafting a high-performance, beautiful desktop user interface.

## 💻 Getting Started

### Running the Application

You can execute the desktop application using the run configurations in your IDE, or run the following Gradle commands from your terminal:

* **Standard Execution**:
  ```bash
  ./gradlew :desktopApp:run
  ```
* **Hot Reload** (Development Mode):
  ```bash
  ./gradlew :desktopApp:hotRun --auto
  ```

## 📄 License

This project is open-source and licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
