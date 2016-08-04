package org.cvrgrid.achuploader;


import java.io.IOException;
import java.text.ParseException;

import org.cvrgrid.achuploader.io.AchLoaderScriptGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Facade for ACH upload script generator.
 * Created by sgranit1 on 8/3/16.
 */
@Component
public class AchUploaderFacade {

    @Autowired
    private String achRoot;

	@Autowired
    private String processedFile;

    @Autowired
    private String batchFile;
    
    @Autowired
    private String subjectPrefix;

    public void generateScript(String aRoot, String limit, String pFile, String bFile, String sPrefix) throws IOException, ParseException {  	
    	if (aRoot.isEmpty()) aRoot = achRoot;
    	if (limit.isEmpty()) limit = "0";
    	if (pFile.isEmpty()) pFile = processedFile;
    	if (bFile.isEmpty()) bFile = batchFile;
    	if (sPrefix.isEmpty()) sPrefix = subjectPrefix;
    	AchLoaderScriptGenerator generator = new AchLoaderScriptGenerator(achRoot, limit, pFile, bFile, sPrefix);
    }

}
