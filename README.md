# Personalmanagement Software

Hochschulprojekt für das Modul 'Objektorientiertes Programmieren'.

## Wie ist es aufzusetzen?

### Abhängigkeiten

Hier sind alle Abhängigkeiten genannt, welche installiert werden müssen:

- Java OpenJDK 24

### Vor dem ersten Ausführen (Optional)

Falls ein bestimmter Speicherpfad für die Datenbank und die Backup-Datenbank verwendet werden soll,
muss dies für die Haupt-Datenbank in `src/main/resources/database.properties` unter dem Feld `db.url` gemacht werden.

Für die Backup-Datenbank muss die Datei `src/main/resources/backup.properties` unter dem Feld `db.url`
angepasst werden.

### CLI-Kommando

- `java -jar [pfad zur jar-datei]`
- Neuste Version: `java -jar docs/jar-history/pm-software-1.4-SNAPSHOT.jar`

## Was ist noch zu beachten?

Das Programm ist nur nutzbar mit den Beispielpasswörtern der Beispielmitarbeiter.
Die H2-Datenbank kann auch als Server laufen, aber hier wird das Programm nur lokal ausgeführt, da sonst der Rahmen
des Projekts gesprengt wird.

Auch werden keine Sicherheitsmaßnahmen für sichere Passwörter oder sichere Daten vorgenommen, da dies erneut unseren
Rahmen sprengen würde.