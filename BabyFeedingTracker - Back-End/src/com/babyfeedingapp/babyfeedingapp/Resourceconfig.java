package com.babyfeedingapp.babyfeedingapp;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class Resourceconfig extends ResourceConfig {
    public Resourceconfig() {
        //Register the classes
    	register(CorsFilter.class);
        register(FeedingInstanceResource.class);
        register(JacksonConfig.class);       

        //Register the packages
        packages("com.babyfeedingapp.babyfeedingapp.CorsFilter");
        packages("com.babyfeedingapp.babyfeedingapp.FeedingInstanceResource");
        packages("com.babyfeedingapp.babyfeedingapp.JacksonConfig");
        packages("com.babyfeedingapp.babyfeedingapp");
    }

}