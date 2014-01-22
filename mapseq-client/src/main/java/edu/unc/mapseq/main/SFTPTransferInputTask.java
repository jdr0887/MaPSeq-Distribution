package edu.unc.mapseq.main;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPTransferInputTask implements Runnable {

    private Session session;

    private String fileName;

    private String remoteDirectory;

    public SFTPTransferInputTask(Session session, String fileName, String remoteDirectory) {
        super();
        this.session = session;
        this.fileName = fileName;
        this.remoteDirectory = remoteDirectory;
    }

    @Override
    public void run() {
        ChannelSftp sftpChannel = null;
        try {
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect(5 * 1000);
            // SftpATTRS attrs = sftpChannel.stat(remoteDirectory);
            sftpChannel.cd(remoteDirectory);
            sftpChannel.get(fileName, fileName, new TransferProgressMonitor(), ChannelSftp.OVERWRITE);
            sftpChannel.chmod(0644, fileName);
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
                sftpChannel.disconnect();
            }
        }
    }

}
