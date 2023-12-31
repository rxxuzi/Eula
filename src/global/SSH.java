package global;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;

import static global.Config.*;

public final class SSH{
    public final String host;
    public final int port;
    public final String user;
    public final String password;
    public final String userHost;

    public SSH() {
        this(SSH_PATH);
    }

    public SSH(String filePath) {
        SSHConfigData configData = loadConfig(filePath);
        this.host = configData.host;
        this.port = configData.port;
        this.user = configData.user;
        this.password = configData.password;
        this.userHost = this.user + "@" + this.host;
    }

    private SSHConfigData loadConfig(String filePath) {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, SSHConfigData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SSHConfigData {
        String host;
        int port;
        String user;
        String password;
    }

    public Session openSession() throws JSchException {
        JSch jsch = new JSch();
        Session session;
        session = jsch.getSession(this.user, this.host, this.port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(this.password);
        return session;
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

