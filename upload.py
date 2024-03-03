import paramiko
import os

def upload_server(device: int):
    # Server-Informationen
    server_ip = '85.215.149.95'
    username = 'bidhub_build_upload'

    if (device == 1):
        private_key_path = 'C:\\Users\\Philip\\OneDrive\\Uni\\Client Server Programmierung\\BidHub\\bidhub_ssh_private_openssh_bidhub_build_upload.ppk'
        local_directory = 'C:\\Users\\Philip\\OneDrive\\Uni\\Client Server Programmierung\\BidHub\\out_server'
    else:
        private_key_path = 'C:\\Users\\phili\\OneDrive\\Uni\\Client Server Programmierung\\BidHub\\bidhub_ssh_private_openssh_bidhub_build_upload.ppk'
        local_directory = 'C:\\Users\\phili\\OneDrive\\Uni\\Client Server Programmierung\\BidHub\\out_server'

    remote_directory = '/bidhub_server/'

    # Erstelle eine SSH-Verbindung
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    mykey = paramiko.RSAKey.from_private_key_file(private_key_path)
    ssh.connect(server_ip, username=username, pkey=mykey)

    # Lösche den Inhalt des entfernten Verzeichnisses
    stdin, stdout, stderr = ssh.exec_command(f'rm -rf {remote_directory}/*')
    stdout.channel.recv_exit_status()  # Warte auf den Abschluss des Befehls

    # Funktion zum rekursiven Hochladen eines Ordners
    def upload_folder(sftp, local, remote):
        for item in os.listdir(local):
            local_path = os.path.join(local, item)
            remote_path = f"{remote.rstrip('/')}/{item}"

            if os.path.isfile(local_path):
                sftp.put(local_path, remote_path)
            else:  # Es ist ein Ordner
                sftp.mkdir(remote_path)
                upload_folder(sftp, local_path, remote_path)

    # Lösche Inhalte
    print("Lösche alte Server-Dateien")
    stdin, stdout, stderr = ssh.exec_command(f'rm -rf {remote_directory}*')

    # Übertrage den Ordner
    sftp = ssh.open_sftp()

    print("Übertrage neue Server-Dateien")
    upload_folder(sftp, local_directory, remote_directory)

    sftp.close()

    # Schließe die SSH-Verbindung
    ssh.close()
    print("Übertragung abgeschlossen")
