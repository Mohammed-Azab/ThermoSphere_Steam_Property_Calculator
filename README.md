
# ThermoSphere: Steam Property Calculator

A sleek and powerful tool designed to calculate steam properties and classify thermodynamic states with precision and efficiency. Developed as part of the **Engineering Thermodynamics (ENME 505)** course at the German International University in Berlin.

---

## Table of Contents
- [Objective](#objective)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

---

## Objective

The **ThermoSphere** project aims to:
- Automate calculations of steam properties such as pressure, temperature, enthalpy, entropy, specific volume, and internal energy using two independent input parameters.
- Identify and classify the thermodynamic state of steam, including:
  - Compressed liquid
  - Saturated liquid
  - Saturated vapor
  - Superheated steam
  - Mixed (liquid-vapor) state
- Enhance the understanding of thermodynamic principles and their real-world applications.

---

## Features

- **Property Calculation**:
  Input two thermodynamic properties to calculate the remaining ones.
  
- **State Identification**:
  Automatically classify the steam state based on input values.
  
- **Flexible Input Options**:
  Supports multiple units (e.g., MPa, bar, kPa for pressure; °C, K for temperature).
  
- **User-Friendly GUI**:
  - Enter inputs via text fields.
  - View results dynamically in an organized format.
  - Visual cues and color coding to indicate steam states.

- **Extensive Documentation**:
  Includes a user manual, code documentation, and example test cases.

---

## Technologies Used

- **Programming Language**: Java
- **GUI Framework**: JavaFX
- **Libraries**:
  - JavaFX for GUI components
  - Apache Commons Math (optional for advanced calculations)
  
- **Data Source**: Reference steam tables from reliable textbooks which exist in the resources file.

---

## Setup and Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/thermosphere.git
   cd thermosphere
   ```

2. **Build the Project**:
   - Use a build tool like Maven or Gradle to manage dependencies and build the project.

3. **Run the Application**:
   - Execute the Java application:
     ```bash
     java -jar target/ThermoSphere-1.0.jar
     ```

---

## Usage

1. **Launch the Program**:
   - Execute the Java application using the command:
     ```bash
     java -jar target/ThermoSphere-1.0.jar
     ```

2. **Input Thermodynamic Properties**:
   - Enter two independent properties (e.g., pressure and temperature) in the provided input fields.
   - Choose the appropriate units from the drop-down menu (if available).

3. **View Results**:
   - The program will calculate the remaining thermodynamic properties.
   - The thermodynamic state (e.g., compressed liquid, saturated vapor) will be displayed.
   - Visual cues (e.g., color coding) will help identify the steam state.

4. **GUI Interaction** (if applicable):
   - Interact with the user-friendly JavaFX interface.
   - Input fields and output results are organized for a seamless experience.

---

## Contributing

We welcome contributions to enhance **ThermoSphere**! Follow these steps:

1. **Fork the Repository**:
   - Navigate to the GitHub project and click the **Fork** button.

2. **Clone the Forked Repository**:
   ```bash
   git clone https://github.com/yourusername/thermosphere.git
   ```

3. **Create a New Branch**:
   - Name your branch based on the feature or bug fix you are addressing:
     ```bash
     git checkout -b feature-name
     ```

4. **Make Changes**:
   - Implement your changes in the appropriate files.
   - Ensure your code is clean and well-commented.

5. **Commit Your Changes**:
   - Write a clear commit message summarizing your updates:
     ```bash
     git commit -m "Description of changes"
     ```

6. **Push the Changes**:
   ```bash
   git push origin feature-name
   ```

7. **Open a Pull Request**:
   - Go to the original repository.
   - Click **New Pull Request** and select your branch.
   - Provide a clear description of the changes and improvements.

8. **Collaborate**:
   - Address feedback or suggestions from reviewers to finalize your contribution.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

**ThermoSphere**: Streamlining thermodynamic calculations for engineers and enthusiasts alike.
