package org.cvrgrid.achuploader;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Annotated application configuration.
 * Created by sgranit1 on 8/3/16.
 */
@Configuration
@ComponentScan(basePackages = "org.cvrgrid.achuploader")
public class ApplicationConfigs {

    @Bean
    public String version() {
        return "v1.0.0, updated 8/3/2016";
    }

    @Bean
    public String dateFormat() {
        return "yyyy-MM-dd HH:mm";
    }

    @Bean
    public org.apache.commons.configuration2.Configuration configurationFile() throws ConfigurationException {
        Configurations configurations = new Configurations();
        return configurations.ini(new File("achuploader.conf"));
    }

    @Bean
    public String achRoot(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("ach-root");
    }

    @Bean
    public String processedFile(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("processed-File");
    }

    @Bean
    public String batchFile(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("batch-File");
    }

}
