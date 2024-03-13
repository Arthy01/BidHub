import paramiko
import time

def start_server(device: int):
    # Server-Informationen
    server_ip = '85.215.149.95'
    username = 'bidhub_server'

    if (device == 1):
        private_key_path = 'C:\\Users\\Philip\\OneDrive\\Uni\\Client Server Programmierung\\BidHub\\bidhub_ssh_private_openssh_bidhub_server.ppk'
    else:
        private_key_path = 'C:\\Users\\phili\\OneDrive\\Uni\\Client Server Programmierung\\BidHub\\bidhub_ssh_private_openssh_bidhub_server.ppk'

    remote_java_install_directory ='/java/installation/jdk-21.0.2/bin/java'
    remote_jar_directory = '/bidhub_server/BidHub_Server.jar'

    # Erstelle eine SSH-Verbindung
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    mykey = paramiko.RSAKey.from_private_key_file(private_key_path)
    ssh.connect(server_ip, username=username, pkey=mykey)

    # Beende den Java-Prozess durch seinen Namen
    try:
        ssh.exec_command(f'pkill -f "{remote_jar_directory}"')
        print("Java-Prozess wurde beendet.")
        time.sleep(1)  # Warte ein paar Sekunden, um sicherzustellen, dass der Prozess beendet wurde
    except Exception as e:
        print(f"Fehler beim Beenden des Java-Prozesses: {e}")

    # Versuche, die bestehende screen-Sitzung zu schließen, falls vorhanden
    try:
        ssh.exec_command('screen -S bidhub_server -X quit')
        print("Bestehende screen-Sitzung 'bidhub_server' wurde geschlossen.")
        time.sleep(1)  # Warte kurz, um sicherzustellen, dass die Sitzung geschlossen wurde
    except Exception as e:
        print(f"Fehler beim Schließen der bestehenden screen-Sitzung: {e}")

    # Starte eine neue screen-Sitzung im Hintergrund und führe den Java-Befehl aus
    java_command = f'authbind --deep {remote_java_install_directory} -jar "{remote_jar_directory}"'
    screen_command = f'screen -dmS bidhub_server bash -c \'{java_command}; exec bash\''
    try:
        ssh.exec_command(screen_command)
        print("Neue screen-Sitzung 'bidhub_server' wurde gestartet und Java-Befehl ausgeführt.")
    except Exception as e:
        print(f"Fehler beim Starten der neuen screen-Sitzung: {e}")

    # Schließe die SSH-Verbindung
    ssh.close()

if __name__ == "__main__":
    start_server(int(input("Device: [1] Home Pc | [2] Surface > ")))