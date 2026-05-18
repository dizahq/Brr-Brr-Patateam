# =========================================================================
# NSIS Installer Script for The Last Stand
# =========================================================================

!define APP_NAME "The Last Stand"
!define COMP_NAME "Brr-Brr-Patateam"
!define VERSION "1.0.0"
!define EXE_NAME "TheLastStand.exe"

# Metadata
Name "${APP_NAME}"
Caption "${APP_NAME} - Setup"
Icon "Windows\icon.ico" ; Uses your game's exe icon for the installer if it has one
OutFile "Windows\TheLastStand_Setup.exe" ; Where the final installer drops
InstallDir "$PROGRAMFILES\${APP_NAME}" ; Default installation folder
RequestExecutionLevel admin ; Guarantees permission to install to Program Files

# -------------------------------------------------------------------------
# Installer Pages
# -------------------------------------------------------------------------
Page directory    ; Asks the user where they want to install the game
Page instfiles    ; Shows the installation progress bar

# -------------------------------------------------------------------------
# Installation Section
# -------------------------------------------------------------------------
Section "MainSection" SEC01
    SetOutPath "$INSTDIR"
    
    ; Pull the executable created by Launch4j
    File "Windows\${EXE_NAME}"
    
    ; Create Start Menu Shortcuts
    CreateDirectory "$SMPROGRAMS\${APP_NAME}"
    CreateShortcut "$SMPROGRAMS\${APP_NAME}\${APP_NAME}.lnk" "$INSTDIR\${EXE_NAME}"
    CreateShortcut "$SMPROGRAMS\${APP_NAME}\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
    
    ; Create Desktop Shortcut
    CreateShortcut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${EXE_NAME}"
    
    ; Generate Uninstaller Registry Entry for Windows Settings -> Apps & Features
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayName" "${APP_NAME}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "UninstallString" '"$INSTDIR\Uninstall.exe"'
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayVersion" "${VERSION}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "Publisher" "${COMP_NAME}"
    
    ; Build the uninstaller engine
    WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

# -------------------------------------------------------------------------
# Uninstallation Section
# -------------------------------------------------------------------------
Section "Uninstall"
    ; Delete shortcuts
    Delete "$DESKTOP\${APP_NAME}.lnk"
    Delete "$SMPROGRAMS\${APP_NAME}\${APP_NAME}.lnk"
    Delete "$SMPROGRAMS\${APP_NAME}\Uninstall.lnk"
    RMDir "$SMPROGRAMS\${APP_NAME}"
    
    ; Delete core files
    Delete "$INSTDIR\${EXE_NAME}"
    Delete "$INSTDIR\Uninstall.exe"
    
    ; Clean up installation directory
    RMDir "$INSTDIR"
    
    ; Clean up registry entry
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}"
SectionEnd