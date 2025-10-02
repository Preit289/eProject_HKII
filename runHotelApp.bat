@echo off
cd /d "C:\Users\hoant\Documents\NetBeansProjects\eProject_HKII\target"
java --enable-native-access=javafx.graphics --module-path "C:\Users\hoant\Downloads\openjfx-24.0.2_windows-x64_bin-sdk (1)\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml -jar HotelManagementSystemFXML-1.0-SNAPSHOT.jar
pause
