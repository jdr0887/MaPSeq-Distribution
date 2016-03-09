package edu.unc.mapseq.dao.soap;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.ws.FileDataService;

/**
 * 
 * @author jdr0887
 */
@Component
public class FileDataDAOImpl extends BaseDAOImpl<FileData, Long> implements FileDataDAO {

    private final Logger logger = LoggerFactory.getLogger(FileDataDAOImpl.class);

    private FileDataService fileDataService;

    public FileDataDAOImpl() {
        super(FileData.class);
    }

    @PostConstruct
    public void init() {
        this.fileDataService = getService().getPort(FileDataService.class);
        Client cl = ClientProxy.getClient(fileDataService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(FileData entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(FileData)");
        Long id = fileDataService.save(entity);
        return id;
    }

    @Override
    public FileData findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        return fileDataService.findById(id);
    }

    @Override
    public List<FileData> findByExample(FileData fileData) {
        logger.debug("ENTERING findByExample(FileData)");
        return fileDataService.findByExample(fileData);
    }

}
