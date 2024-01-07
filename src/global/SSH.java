package global;

import com.jcraft.jsch.*;

import java.io.*;

import static global.Config.fast;

public class SSH implements AutoCloseable{
    public final String host;
    public final int port;
    public final String user;
    public final String password;
    public final String userHost;

    private Session session;

    public SSH() {
        this(fast.ssh.host, fast.ssh.port, fast.ssh.user, fast.ssh.password);
    }

    public SSH(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.userHost = this.user + "@" + this.host;
    }

    public void open() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(this.user, this.host, this.port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(this.password);
        session.connect();
    }

    @Override
    public void close() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public String getSessionInfo() {
        if (session == null || !session.isConnected()) {
            return "No active SSH session.";
        }
        return "SSH Session Info: Host=" + session.getHost() + ", Port=" + session.getPort() + ", User=" + session.getUserName();
    }

    public void send(String localPath, String remotePath) throws JSchException, SftpException {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("SSH session not connected.");
        }
        ChannelSftp sftpChannel = null;
        try {
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.put(localPath, remotePath);
        } finally {
            if (sftpChannel != null && sftpChannel.isConnected()) {
                sftpChannel.exit();
                sftpChannel.disconnect();
            }
        }
    }

    public void send(File localFile, String remotePath) throws JSchException, SftpException, IOException {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("SSH session not connected.");
        }
        ChannelSftp sftpChannel = null;
        try (FileInputStream fis = new FileInputStream(localFile)){
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.put(fis, remotePath);
        } finally {
            if (sftpChannel != null && sftpChannel.isConnected()) {
                sftpChannel.exit();
                sftpChannel.disconnect();
            }
        }
    }

    public void get(String remotePath, String localPath) throws JSchException, SftpException, IOException {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("SSH session not connected.");
        }
        ChannelSftp sftpChannel = null;
        try {
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.get(remotePath, localPath);
        } finally {
            if (sftpChannel != null && sftpChannel.isConnected()) {
                sftpChannel.exit();
                sftpChannel.disconnect();
            }
        }
    }

    public String exec(String command) throws JSchException, IOException {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("SSH session not connected.");
        }
        String output;
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            try (InputStream in = channel.getInputStream()) {
                channel.connect();
                output = new String(in.readAllBytes());
            }
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        return output;
    }

    public String output(Channel channel) throws IOException, JSchException {
        InputStream input = channel.getInputStream();
        channel.connect();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        return output.toString().trim();
    }
}

