package edu.unc.mapseq.ws.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.ws.FileDataService;

public class FileDataServiceImpl implements FileDataService {

    private final Logger logger = LoggerFactory.getLogger(FileDataServiceImpl.class);

    private FileDataDAO fileDataDAO;

    public FileDataServiceImpl() {
        super();
    }

    @Override
    public FileData findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        FileData entity = null;
        if (id == null) {
            logger.warn("id is null");
            return entity;
        }
        try {
            entity = fileDataDAO.findById(id);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return entity;
    }

    @Override
    public Long save(FileData entity) {
        logger.debug("ENTERING save(FileData)");
        try {
            fileDataDAO.save(entity);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return entity.getId();
    }

    @Override
    public DataHandler download(Long id) {
        logger.debug("ENTERING download(Long)");
        if (id == null) {
            logger.warn("id is null");
            return null;
        }
        FileData fileData = null;
        try {
            fileData = fileDataDAO.findById(id);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        File file = new File(fileData.getPath(), fileData.getName());
        DataHandler dataHandler = new DataHandler(new FileDataSource(file));
        return dataHandler;
    }

    @Override
    public Long upload(DataHandler data, String flowcell, String workflow, String name, String mimeType) {
        logger.debug("ENTERING upload(Holder<DataHandler>, Holder<String>, Holder<String>, Holder<String>, Holder<String>)");
        FileData fileData = null;

        String path = String.format("%s/%s/%s", System.getenv("MAPSEQ_OUTPUT_DIRECTORY"), flowcell, workflow);
        logger.info("path: {}", path);
        File file = new File(path, name);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            InputStream mtomIn = data.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            IOUtils.copyLarge(mtomIn, fos);
            mtomIn.close();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        fileData = new FileData();
        fileData.setMimeType(MimeType.valueOf(mimeType));
        fileData.setName(name);
        fileData.setPath(path);
        try {
            Long id = fileDataDAO.save(fileData);
            fileData.setId(id);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }

        return fileData.getId();
    }

    @Override
    public List<FileData> findByExample(FileData fileData) {
        logger.debug("ENTERING findByExample(FileData)");
        List<FileData> ret = null;
        try {
            ret = fileDataDAO.findByExample(fileData);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    public FileDataDAO getFileDataDAO() {
        return fileDataDAO;
    }

    public void setFileDataDAO(FileDataDAO fileDataDAO) {
        this.fileDataDAO = fileDataDAO;
    }

}
