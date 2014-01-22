package edu.unc.mapseq.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPTransferOutputTask implements Runnable {

    private Session session;

    private File file;

    private String remoteDirectory;

    public SFTPTransferOutputTask(Session session, File file, String remoteDirectory) {
        super();
        this.session = session;
        this.file = file;
        this.remoteDirectory = remoteDirectory;
    }

    @Override
    public void run() {
        ChannelSftp sftpChannel = null;
        FileInputStream fis = null;
        try {
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect(5 * 1000);
            // SftpATTRS attrs = sftpChannel.stat(remoteDirectory);
            sftpChannel.cd(remoteDirectory);
            fis = new FileInputStream(file);
            sftpChannel.put(fis, file.getName(), new TransferProgressMonitor(), ChannelSftp.OVERWRITE);
            sftpChannel.chmod(0644, file.getName());
        } catch (FileNotFoundException | JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sftpChannel != null) {
                sftpChannel.exit();
                sftpChannel.disconnect();
            }
        }

    }

}
