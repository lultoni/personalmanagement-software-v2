# Personalmanagement Software

Hochschulprojekt für das Modul 'Objektorientiertes Programmieren'.

## Wie ist es aufzusetzen?

### Abhängigkeiten

Hier sind nur alle Abhängigkeiten genannt, welche sich nicht automatisch installieren:

- Java OpenJDK 23.0.1

Automatisch wird Folgendes installiert (falls nicht vorhanden):

- Maven
- (?) H2-Datenbanken
- (?) Google Gson

### CLI-Kommando

- `java -jar [pfad zur jar-datei]`
- Neuste Version: `java -jar docs/jar-history/pm-software-1.2-SNAPSHOT.jar`

## Was ist noch zu beachten?

Das Programm ist nur nutzbar mit den Beispielpasswörtern der Beispielmitarbeiter.
Die H2-Datenbank kann auch als Server laufen, aber hier wird das Programm nur lokal ausgeführt, da sonst der Rahmen
des Projekts gesprengt wird.