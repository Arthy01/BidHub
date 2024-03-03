import os
import subprocess
import shutil

def compile_java_files(src_path, output_dir, main_class, javafx_path=None):
    # Erstelle das Ausgabeverzeichnis, falls es nicht existiert
    os.makedirs(output_dir, exist_ok=True)

    is_server_application = main_class.find("ServerApplication") != -1

    # Sammle alle Java-Dateien
    java_files = []
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if file.endswith(".java"):
                if (is_server_application):
                    java_files.append(os.path.join(root, file))
                elif (not is_server_application and not contains_after_src(root, "server")):
                    if (file.find("ServerApplication") != -1): continue

                    java_files.append(os.path.join(root, file))

    # Kompiliere die gesammelten Java-Dateien
    compile_command = ["javac"]
    if javafx_path:
        compile_command.extend(["--module-path", javafx_path, "--add-modules", "javafx.controls,javafx.fxml"])
    compile_command.extend(["-d", output_dir] + java_files)
    subprocess.run(compile_command)

    # Ausführungsbefehl für die Anwendung
    run_command = ["java", "-cp", "\"" + output_dir + "\""]
    if javafx_path:
        run_command.extend(["--module-path", "\"" + javafx_path + "\"", "--add-modules", "javafx.controls,javafx.fxml"])
    run_command.append(main_class)

    print(f"Um die {main_class} Anwendung auszuführen, benutze den folgenden Befehl:")
    print(" ".join(run_command))

def copy_resources(src_resources_path, dest_path):
    dest_resources_path = os.path.join(dest_path, "de/hwrberlin/bidhub")
    if os.path.exists(src_resources_path):
        shutil.copytree(src_resources_path, dest_resources_path, dirs_exist_ok=True)

def contains_after_src(path, server_or_client):
    # Zuerst suchen wir nach dem Substring '\src\'
    src_index = path.find('\\src\\')

    if src_index != -1:
        # Extrahieren des Teilstrings nach '\src\'
        sub_path = path[src_index + len('\\src\\'):]
        
        # Überprüfen, ob '\server' in dem extrahierten Teilstring enthalten ist
        return f'\\{server_or_client}' in sub_path
    else:
        return False

def create_jar(output_dir, jar_name, main_class):
    # Stelle sicher, dass das Output-Verzeichnis existiert
    if not os.path.exists(output_dir):
        print(f"Das Verzeichnis {output_dir} existiert nicht.")
        return

    # Pfad für die JAR-Datei
    jar_path = os.path.join(output_dir, jar_name)

    # Erstellen des Manifest-Datei-Inhalts
    manifest_content = f"Manifest-Version: 1.0\nMain-Class: {main_class}\n\n"
    manifest_file_path = os.path.join(output_dir, "MANIFEST.MF")

    # Schreiben der Manifest-Datei
    with open(manifest_file_path, "w") as manifest_file:
        manifest_file.write(manifest_content)

    # JAR-Befehl ausführen
    subprocess.run(["jar", "cvfm", jar_path, manifest_file_path, "-C", output_dir, "."], check=True)

    print(f"JAR-Datei erstellt: {jar_path}")

def copy_directory(src, dest):
    try:
        # Kopiere das gesamte Verzeichnis
        os.makedirs(dest, exist_ok=True)
        shutil.copytree(src, dest, dirs_exist_ok=True)
        print(f"Verzeichnis erfolgreich von {src} nach {dest} kopiert.")
    except shutil.Error as e:
        print(f"Fehler beim Kopieren des Verzeichnisses: {e}")
    except OSError as e:
        print(f"Fehler beim Kopieren des Verzeichnisses: {e}")

if __name__ == "__main__":
    try:
        mode = int(input("Modes:\n[1] Build Only\n[2] Build and upload\n[3] Build, upload and start server\n\nMode > "))
    except:
        print("Invalid input!")
        input("Press enter to exit")
        exit()

    if (mode not in range(1, 4)):
        print("Invalid mode!")
        input("Press enter to exit")
        exit()

    script_directory = os.path.dirname(os.path.realpath(__file__))
    src_directory = os.path.join(script_directory, "src")
    resources_directory = os.path.join(src_directory, "main", "resources", "de", "hwrberlin", "bidhub")

    try:
        device = int(input("Devices:\n[1] Home PC\n[2] Surface\n\nDevice > "))
    except:
        print("Invalid input!")
        input("Press enter to exit")
        exit()

    if (device not in range(1, 3)):
        print("Invalid device!")
        input("Press enter to exit")
        exit()

    # Pfad zum JavaFX 'lib' Ordner
    javafx_lib_path_homepc = "C:\\Users\\Philip\\OneDrive\\Programming Stuff\\JavaFX-Installation\\javafx-sdk-19\\lib"
    javafx_lib_path_surface = "C:\\Users\\phili\\OneDrive\\Programming Stuff\\JavaFX-Installation\\javafx-sdk-19\\lib"

    javafx_base_path_homepc = "C:\\Users\\Philip\\OneDrive\\Programming Stuff\\JavaFX-Installation\\javafx-sdk-19"
    javafx_base_path_surface = "C:\\Users\\phili\\OneDrive\\Programming Stuff\\JavaFX-Installation\\javafx-sdk-19"

    if (device == 1):
        javafx_lib_path = javafx_lib_path_homepc
        javafx_base_path = javafx_base_path_homepc
    else:
        javafx_lib_path = javafx_lib_path_surface
        javafx_base_path = javafx_base_path_surface

    # Setze die erforderlichen Pfade und Ausschlüsse
    client_output = os.path.join(script_directory, "out_client")
    server_output = os.path.join(script_directory, "out_server")

    # Lösche den Ordner out_client
    if os.path.exists(client_output):
        shutil.rmtree(client_output)
        print(f"Ordner {client_output} wurde gelöscht.")

    # Lösche den Ordner out_server
    if os.path.exists(server_output):
        shutil.rmtree(server_output)
        print(f"Ordner {server_output} wurde gelöscht.")

    # Kompiliere und erstelle Ausführungsbefehle für Client und Server
    compile_java_files(src_directory, client_output, "de.hwrberlin.bidhub.ClientApplication", javafx_lib_path)
    compile_java_files(src_directory, server_output, "de.hwrberlin.bidhub.ServerApplication", javafx_lib_path)

    # Kopiere Ressourcen in das Output-Verzeichnis
    copy_resources(resources_directory, client_output)
    copy_resources(resources_directory, server_output)

    #JavaFX installation kopieren
    copy_directory(javafx_base_path, os.path.join(client_output, "lib\\javafx"))

    # JAR erstellen
    create_jar(client_output, "Bidhub - Client.jar", "de.hwrberlin.bidhub.ClientApplication")
    create_jar(server_output, "Bidhub - Server.jar", "de.hwrberlin.bidhub.ServerApplication")

    # Inhalt der .bat-Datei
    bat_content = 'java --module-path "lib\\javafx\\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar "Bidhub - Client.jar"'

    # Vollständiger Pfad zur .bat-Datei
    bat_file_path = f"{client_output}\\start_bidhub_client.bat"

    # .bat-Datei erstellen und schreiben
    with open(bat_file_path, "w") as bat_file:
        bat_file.write(bat_content)

    print(f".bat-Datei erstellt unter: {bat_file_path}")

    if (mode == 2 or mode == 3):
        import upload
        upload.upload_server(device)
    
    if (mode == 3):
        import start
        start.start_server(device)

    print("Excecution finished!")
        

# START COMMAND CLIENT: java --module-path "lib\javafx\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar "Bidhub - Client.jar"
# START COMMAND SERVER: java -jar "Bidhub - Server.jar"