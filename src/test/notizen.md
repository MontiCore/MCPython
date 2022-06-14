# Testnotizen

Folgende Tests werden für SiPython-Projekt benötigt:
- [ ] aus einem .sipyton-Script erfolgreich ein .py-Script generieren
- [ ] aus einem .sipyton-Script ein .py-Script generieren und das Ergebnis mit einem exisierenden, korrekten .py-Script vergleichen
- [ ] test Python-CoCos

Folgende Klassen müssen dafür implementiert werden:
- [ ] Generator: liest ein .sipython-Script ein und generiert ein .py-Script
- [ ] PythonPrettyPrinter: liest ein AST-Baum eines .sipython-Scripts ein und erzeugt ein .py-Script als String
- [ ] create CoCos for SiPython-Grammar