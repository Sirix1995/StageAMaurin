Name "GroIMP"
InstallDir "$PROGRAMFILES\GroIMP ${VERSION}\"
LicenseData LICENSE
OutFile GroIMP-${VERSION}-win32.exe
Var MAXHEAP
Var JAVAW

Function GetJRE
;
;  Find JRE (Javaw.exe)
;  1 - in JAVA_HOME environment variable
;  2 - in the registry
;  3 - assume javaw.exe in current dir or PATH
 
  Push $R0
  Push $R1
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"
 
  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"
        
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd

Function .onInit
	InitPluginsDir
	File "/oname=$PLUGINSDIR\GroIMP_Options.ini" options.ini
	System::Alloc 64
	Pop $1
	System::Call "*$1(i 64)"
	System::Call "Kernel32::GlobalMemoryStatusEx(i) v (r1)"
	System::Call "*$1(i .r2, i .r3, l .r4, l .r5, \
	                  l .r6, l .r7, l .r8, l .r9)"
	System::Free $1

#	DetailPrint "Structure size (useless): $2 Bytes"
#	DetailPrint "Memory load: $3%"
#	DetailPrint "Total physical memory: $4 Bytes"
#	DetailPrint "Free physical memory: $5 Bytes"
#	DetailPrint "Total page file: $6 Bytes"
#	DetailPrint "Free page file: $7 Bytes"
#	DetailPrint "Total virtual: $8 Bytes"
#	DetailPrint "Free virtual: $9 Bytes"

	System::Int64Op $4 / 1600000
	Pop $0
	IntCmp $0 1500 memOK memOK

	StrCpy $0 "1500"
	
memOK:
	WriteINIStr "$PLUGINSDIR\GroIMP_Options.ini" "Field 2" State $0

	Push $0
	Call GetJRE
	Pop $JAVAW
	SearchPath $0 $JAVAW
#	DetailPrint "JVM Path: $JAVAW $0"
	IfErrors 0 java_found
		MessageBox MB_YESNO|MB_ICONEXCLAMATION "Error: $\n\
			Java seems to be not installed on this system.$\n\
			Please download and install a Java Runtime Environment (JRE),$\n\
			for instance from: http://www.java.com$\n\
			$\n\
			Do you want to continue with the installation anyways ?"\
			IDYES dont_abort
		Abort
		dont_abort:
	java_found:
	Pop $0
FunctionEnd

Function optionsPage
	InstallOptions::dialog "$PLUGINSDIR\GroIMP_Options.ini"
	Pop $R0
	ReadINIStr $MAXHEAP "$PLUGINSDIR\GroIMP_Options.ini" "Field 2" "State"
FunctionEnd


Page license
Page components
Page directory
Page custom optionsPage "" ": Options"
Page instfiles
UninstPage uninstConfirm
UninstPage instfiles


Section "GroIMP ${VERSION}"
	SetOutPath $INSTDIR
	File /r ..\app\*.*
	File groimp.ico
	
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\GroIMP ${VERSION}" "DisplayName" "GroIMP ${VERSION} (remove only)"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\GroIMP ${VERSION}" "UninstallString" "$INSTDIR\Uninstall.exe"
	WriteUninstaller $INSTDIR\Uninstall.exe
SectionEnd

Section "Startmenu Icon"
	;$SMPROGRAMS
	SetOutPath $INSTDIR
	CreateDirectory "$SMPROGRAMS\GroIMP ${VERSION}"
	CreateShortCut  "$SMPROGRAMS\GroIMP ${VERSION}\GroIMP.lnk" "$JAVAW" "-Xmx$MAXHEAPM -jar core.jar" "$INSTDIR\groimp.ico" 0
	CreateShortCut  "$SMPROGRAMS\GroIMP ${VERSION}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
SectionEnd

/*
Section "Quicklaunch Icon"
	;$QUICKLAUNCH
SectionEnd

Section "Desktop Icon"
	;$DESKTOP
SectionEnd
*/


Section "Uninstall"
	Delete "$SMPROGRAMS\GroIMP ${VERSION}\GroIMP.lnk"
	Delete "$SMPROGRAMS\GroIMP ${VERSION}\Uninstall.lnk"
	RMDir  "$SMPROGRAMS\GroIMP ${VERSION}"
	Delete $INSTDIR\Uninstall.exe
	RMDir /r $INSTDIR\plugins
	RMDir /r $INSTDIR\ext
	Delete $INSTDIR\*.*
	RMDir $INSTDIR
	
;	DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\GroIMP"
	DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\GroIMP ${VERSION}"
SectionEnd
