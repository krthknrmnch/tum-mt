package com.tum.in.cm.probeservice.component;

import com.tum.in.cm.probeservice.config.ProbeStatusSingletonBeanConfig;
import com.tum.in.cm.probeservice.util.Constants;
import com.tum.in.cm.probeservice.web.ws.dto.ArbitraryMeasurementSpecification;
import com.tum.in.cm.probeservice.web.ws.dto.InternalMeasurementRequestObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.tum.in.cm.probeservice.util.Utils.*;

@Component
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class StopArbitraryMeasurementTask implements Runnable {
    private String connectorIpPort;

    private InternalMeasurementRequestObject measurementRequestObject;

    private long execTimestamp;

    private String containerId;

    @Override
    public void run() {
        log.info("Attempting to stop container for arbitrary measurement");
        String userHomeDirectory = System.getProperty("user.home");
        try {
            long execStopTimestamp = Instant.now().getEpochSecond();
            executeProcessAndReturnOutput(Arrays.asList("docker", "stop", containerId), 120);
            log.info("Compressing result data for arbitrary measurement");
            String measurementDirectory = userHomeDirectory
                    + "/probe/"
                    + measurementRequestObject.getMeasurementId();
            String probeDirectory = measurementDirectory
                    + "/"
                    + measurementRequestObject.getProbeId();
            String measurementZip = userHomeDirectory
                    + "/probe/"
                    + execTimestamp
                    + "_"
                    + measurementRequestObject.getMeasurementId()
                    + "_"
                    + measurementRequestObject.getProbeId()
                    + ".zip";
            FileOutputStream fos = new FileOutputStream(measurementZip);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(probeDirectory);
            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
            log.info("Sending result data to connector for arbitrary measurement");
            boolean isResultUploadSuccess = sendResultDataToConnector(measurementRequestObject, execTimestamp, execStopTimestamp, connectorIpPort, Files.readAllBytes(Paths.get(measurementZip)), true, true);
            //Remove container
            executeProcessAndReturnOutput(Arrays.asList("docker", "rm", "--force", containerId), 120);
            //Remove image
            ArbitraryMeasurementSpecification measurementSpecification = (ArbitraryMeasurementSpecification) measurementRequestObject.getMeasurementSpecification();
            executeProcessAndReturnOutput(Arrays.asList("docker", "image", "rm", "--force", measurementSpecification.getContainerImagePath()), 120);
            //Remove result directory
            executeProcessAndReturnOutput(Arrays.asList("rm", "-rf", measurementDirectory), 60);
            if (isResultUploadSuccess) {
                //Remove zipped file only if result upload successful. Else, keep it for the future for a possible result reupload when feature added.
                executeProcessAndReturnOutput(Arrays.asList("rm", "-f", measurementZip), 60);
            }
        } catch (Exception exception) {
            log.error("Error trying to stop arbitrary measurement");
            ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
            ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean probeStatusSingletonBean = applicationContext.getBean(ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean.class);
            probeStatusSingletonBean.setConnected();
            updateProbeStatus(measurementRequestObject.getProbeId(), Constants.ProbeStatus.CONNECTED, connectorIpPort);
        }
    }

    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws Exception {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
