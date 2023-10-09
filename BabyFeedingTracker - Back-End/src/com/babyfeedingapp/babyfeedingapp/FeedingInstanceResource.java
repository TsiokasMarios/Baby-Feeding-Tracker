package com.babyfeedingapp.babyfeedingapp;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Path("/feeding")
@DeclareRoles({"ADMIN","PHYSICIAN"})
public class FeedingInstanceResource {
    private FeedingInstanceDAO feedingInstanceDAO;

    public FeedingInstanceResource() {
    	//Get the DAO instance
        feedingInstanceDAO = FeedingInstanceDAO.getInstance();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFeedingInstance(FeedingInstance feedingInstance) throws SQLException {
        //Call the DAO to insert the instance
    	//And store the result
        boolean inserted = feedingInstanceDAO.insertFeedingInstance(feedingInstance);

        if (inserted) {//If true return OK status
            return Response.status(Response.Status.CREATED).build();
        } else {//Otherwise bad request
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @RolesAllowed("ADMIN")
    @Path("/{id}")
    public Response deleteFeedingInstance(@PathParam("id") int id) throws SQLException {
        //Delete the feeding instance with the specified ID
    	//And store the result
        boolean deleted = feedingInstanceDAO.deleteFeedingInstance(id);

        if (deleted) {//If successful return OK
            return Response.status(Response.Status.OK).build();
        } else {//Bad request
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @PUT
    @RolesAllowed("ADMIN")
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateFeedingInstance(@PathParam("id") int id, FeedingInstance feedingInstance) throws SQLException {
        //Set the feeding instance ID
    	feedingInstance.setId(id);
    	//Pass the feeding instance to the DAO
    	//Store result
        boolean updated = feedingInstanceDAO.updateFeedingInstance(feedingInstance);

        if (updated) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @PermitAll()
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getFeedingInstance(@PathParam("id") int id) throws SQLException {
        FeedingInstance feedingInstance = feedingInstanceDAO.getFeedingInstance(id);

        if (feedingInstance != null) {
            return Response.ok(feedingInstance).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @PermitAll()
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFeedingInstances(@QueryParam("start") String start, @QueryParam("end") String end) throws SQLException 
    {
        if (start == null && end == null) {//If start and end time are null
        	//Get all the feeding instances
            List<FeedingInstance> feedingInstances = feedingInstanceDAO.getAllFeedingInstances();
            return Response.ok(feedingInstances).build();
        } 
        else {//Otherwise
        	//Create formatter to format the date appropriately
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(start, formatter);
            LocalDateTime endDate = LocalDateTime.parse(end, formatter);
            //Call the DAO method to get the instances for the time period specified
            List<FeedingInstance> feedingInstances = feedingInstanceDAO.getFeedingInstancesByTimePeriod(startDate, endDate);
            return Response.ok(feedingInstances).build();        
            }
    }


    @GET
    @PermitAll()
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/average-milk-consumed")
    public double getAverageMilkConsumed(@QueryParam("start") String start, @QueryParam("end") String end) throws SQLException 
    {
        if (start == null && end == null) {
            // If no time period is specified, calculate average milk consumed for all feeding sessions
            return feedingInstanceDAO.getAverageMilkConsumedPerSession();
        } 
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(start, formatter);
            LocalDateTime endDate = LocalDateTime.parse(end, formatter);
            // Calculate average milk consumed within the specified time period
            return feedingInstanceDAO.calculateAverageMilkConsumedPerFeedingPerSession(startDate, endDate);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll()
    @Path("/average-feeding-duration")
    public Response getAverageDuration() throws SQLException {
        return Response.ok(
        		feedingInstanceDAO.getAverageFeedingSessionDuration()
        		,MediaType.APPLICATION_JSON).build();
    }

    @GET
    @PermitAll()
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/charts")
    public Response getChartData(@QueryParam("start") String start, @QueryParam("end") String end) throws SQLException {
    	//Declare variables
    	List<FeedingInstance> feedingInstances;
    	List<String> labels;
    	JsonArray labelsArray;
    	
    	if (start == null && end == null) {//If start and end time are not null
    		//Get all feeding instances
    		feedingInstances = feedingInstanceDAO.getAllFeedingInstances();
    		labels= getLabelsForAllData(feedingInstances);
    		labelsArray = Json.createArrayBuilder(labels).build();
    	}
    	else {
    		//Create formatter to format and parse the dates
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(start, formatter);
            LocalDateTime endDate = LocalDateTime.parse(end, formatter);
            
            //Get all feeding instances for the specified time period
            
            feedingInstances = feedingInstanceDAO.getFeedingInstancesByTimePeriod(startDate, endDate);
            //Get all the labels
            labels= getLabelsForTimePeriod(startDate, endDate);
            //And convert them to a JSON array
            labelsArray = Json.createArrayBuilder(labels).build();
    	}
        

        // Create two lists for the amount of milk consumed and feeding duration
        List<Double> milk = new ArrayList<>();
        List<Double> duration = new ArrayList<>();

        // Loop through the feeding instances list to collect the milk and duration values
        for (FeedingInstance feedingInstance : feedingInstances) {
        	//Accumulate the milk
            milk.add(feedingInstance.getAmountMl());
            //Calculate the duration between the two dates
            long durationSeconds = Duration.between(
            		feedingInstance.getStartTime(),
            		feedingInstance.getEndTime()).getSeconds();//For appropriate type conversion
            //Calculate the result
            int durationMinutes = (int) (durationSeconds / 60);
            //Add it to the list
            duration.add((double) durationMinutes);
        }      

        
        //////////////////////////////////////////////////////////////////////////////////////
        
        //Do operations for feeding duration data
		JsonArrayBuilder durationArray = Json.createArrayBuilder();
		//Add each feeding duration value to the array
        for (Double feedingDuration : duration) {
            durationArray.add(feedingDuration);
        }
        //Build the array
        JsonArray feedingDurationArray = durationArray.build();
        //Create the json object builder
        JsonObjectBuilder feedingDuration = Json.createObjectBuilder();
        feedingDuration.add("feedingDuration", durationArray.build());
        //////////////////////////////////////////////////////////////////////////////////////
        
        //Similar operations that were done for the milk
        JsonArrayBuilder milkArray = Json.createArrayBuilder();
        for (Double milkConsumed : milk) {
            milkArray.add(milkConsumed);
        }
        JsonArray milkConsumedArray = milkArray.build();
        JsonObjectBuilder milkConsumed = Json.createObjectBuilder();
        milkConsumed.add("milkConsumed", milkArray.build());
        
        //////////////////////////////////////////////////////////////////////////////////////

        JsonObject chartData = Json.createObjectBuilder()
        		//Add the labels
                .add("labels", labelsArray)
                //Add the values by
                .add("datasets", Json.createArrayBuilder()
                		//First adding the amount of milk consumed
                        .add(Json.createObjectBuilder()
                        		//Add label
                                .add("label", "Amount of Milk Consumed")
                                //Add the data
                                .add("data", milkConsumedArray)
                                //Do not fill the area under the line
                                .add("fill", false)
                                //Border color of the line
                                .add("borderColor", "rgb(75, 192, 192)")
                                //How smooth should the line be
                                .add("lineTension", 0.1))
                        //Secondly adding the feeding duration similarly to the milk consumed
                        .add(Json.createObjectBuilder()
                                .add("label", "Feeding Duration (minutes)")
                                .add("data", feedingDurationArray)
                                .add("fill", false)
                                .add("borderColor", "rgb(255, 99, 132)")
                                .add("lineTension", 0.1)))
                .build();//Builds the object

        // Return the JSON object as a response
        return Response.ok(chartData.toString()).build();
    }


    private List<String> getLabelsForTimePeriod(LocalDateTime start, LocalDateTime end) {
    	//Initialize string list
        List<String> labels = new ArrayList<>();
        LocalDateTime date = start;
        while (date.isBefore(end)) {
        	//Add the date in the label
            labels.add(
            		//By formating it to the appropriate format
            		date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))
            		);
            //Move time forward
            date = date.plusHours(1);
        }
        return labels;
    }
    
    private List<String> getLabelsForAllData(List<FeedingInstance> feedingInstances) {
        List<String> labels = new ArrayList<>();
        //Similar to the above function
        for (FeedingInstance feedingInstance : feedingInstances) {
            labels.add(feedingInstance.getStartTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        }
        return labels;
    }

}
