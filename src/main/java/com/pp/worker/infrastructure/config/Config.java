package com.pp.worker.infrastructure.config;

import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

@Service
public class Config {

    public String getQueue() {
        return "dev_" + getResourceBundle().getString("aws.queue");
    }

    public int getBatchSize() {
        return Integer.parseInt(getResourceBundle().getString("aws.batchsize"));
    }

    public int getDelay() {
        return Integer.parseInt(getResourceBundle().getString("aws.delay"));
    }

    protected ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("config");
    }

}
