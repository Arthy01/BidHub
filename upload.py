import paramiko
import os

# Server-Informationen
server_ip = '212.227.233.17'
username = 'bidhub'
private_key_path = 'C:\\Users\\Philip\\Desktop\\Wichtig\\Strato\\strato_ssh_private_bidhub_openssh'

# Pfad des lokalen Ordners und des entfernten Verzeichnisses
local_directory = 'C:\\Users\\Philip\\OneDrive\\Uni\\Client Server Programmierung\\BidHub\\out_server'
remote_directory = '/bidhub'

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
        remote_path = os.path.join(remote, item)

        if os.path.isfile(local_path):
            sftp.put(local_path, remote_path)
        else:  # Es ist ein Ordner
            sftp.mkdir(remote_path, ignore_existing=True)
            upload_folder(sftp, local_path, remote_path)

# Übertrage den Ordner
sftp = ssh.open_sftp()
print("AAAA")
upload_folder(sftp, local_directory, remote_directory)
print("BBBB")
sftp.close()

# Schließe die SSH-Verbindung
ssh.close()
