package com.babyfeedingapp.babyfeedingapp;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedingInstanceDAO {
    private static FeedingInstanceDAO instance;
    
    //////////////////////////////////////////////////////////////////
    //Singleton instance design pattern
    private FeedingInstanceDAO(){

    }

    public static FeedingInstanceDAO getInstance(){
        if (instance == null){
            instance = new FeedingInstanceDAO();
        }
        return instance;
    }
    
    //////////////////////////////////////////////////////////////////

    //Creates connection to the database
    public static Connection createCon() {

        
        try {//Verify the driver exists
        	Class.forName("org.postgresql.Driver");
        	} catch (ClassNotFoundException e) {
        		e.printStackTrace();
        	}
        
        try {
            //Connect to the database
            Connection c = DriverManager.getConnection(
            		"jdbc:postgresql://ec2-52-215-68-14.eu-west-1.compute.amazonaws.com/d8hujnd9a5k8ge"
            		,"adhismwyeiqwhp"
            		,"04debbc7d4d8237efb3757c1171d1bef5b88c18a4eff00a7cfe2b4e384983c11");
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    

    public boolean insertFeedingInstance(FeedingInstance feedingInstance) throws SQLException {

        try (Connection connection = createCon()) {//Connect to database
        	//Create SQL query
            String sql = "INSERT INTO feeding (amount_ml, start_time, end_time) VALUES (?, ?, ?)";
            //Create the statement
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            	//Insert the data to the statement
                statement.setDouble(1, feedingInstance.getAmountMl());
                statement.setTimestamp(2, Timestamp.valueOf(feedingInstance.getStartTime()));
                statement.setTimestamp(3, Timestamp.valueOf(feedingInstance.getEndTime()));
                //Execute and store whether the affected rows are above 0
                boolean result = statement.executeUpdate() > 0;
                connection.close();//Close DB connection
                return result;
            }
        }
    }

    public boolean deleteFeedingInstance(int id) throws SQLException {
        try (Connection connection = createCon()) {//Connect to database
        	//Create SQL query
            String sql = "DELETE FROM feeding WHERE id = ?";
            //Create the statement
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            	//Insert the data to the statement
                statement.setInt(1, id);
                //Execute and store whether the affected rows are above 0
                boolean result = statement.executeUpdate() > 0;
    			connection.close();//Close DB connection
    			return result;
            }
        }
    }

    public FeedingInstance getFeedingInstance(int id) throws SQLException {
        try (Connection connection = createCon()) {//Connect to database
        	//Create SQL query
            String sql = "SELECT id, amount_ml, start_time, end_time FROM feeding WHERE id = ?";
            //Create the statement
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            	//Insert the data to the statement
                statement.setInt(1,id);
                try (ResultSet resultSet = statement.executeQuery()) {
                	//If there are results in the result set
                    if (resultSet.next()) {
                    	connection.close();//Close the connection
                    	//And create a feeding instance
                    	//By getting the values from the resultSet
                    	FeedingInstance instance = new FeedingInstance(
                                resultSet.getInt("id")
                                ,resultSet.getDouble("amount_ml")
                                ,resultSet.getTimestamp("start_time").toLocalDateTime()
                                ,resultSet.getTimestamp("end_time").toLocalDateTime()
                        );
                        return instance;
                    }
                }
            }
            connection.close();
            return null;
        }
    }
    
    public boolean updateFeedingInstance(FeedingInstance feedingInstance) throws SQLException{
    	try (Connection connection = createCon()){//Connect to database
    		//Create SQL query
    		String sql = "UPDATE feeding SET amount_ml = ?, start_time = ?, end_time = ? WHERE id = ?";
    		//Create the statement
    		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
    			//Insert the data to the statement
    			preparedStatement.setDouble(1, feedingInstance.getAmountMl());
    			preparedStatement.setTimestamp(2, Timestamp.valueOf(feedingInstance.getStartTime()));
    			preparedStatement.setTimestamp(3, Timestamp.valueOf(feedingInstance.getEndTime()));
    			preparedStatement.setInt(4, feedingInstance.getId());
    			//Execute and store whether the affected rows are above 0
    			boolean result = preparedStatement.executeUpdate() > 0;
    			connection.close();//Close the connection
    			return result;
    		}
    	}
    }

    public List<FeedingInstance> getAllFeedingInstances() throws SQLException {
        try (Connection connection = createCon()) {//Connect to database
            List<FeedingInstance> feedingInstances = new ArrayList<>();//Initialize feeding instances list
            //Create SQL query
            String sql = "SELECT id, amount_ml, start_time, end_time FROM feeding ORDER BY id";
            //Create the statement
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            	//Call getResults method with the list and the statement
                getResults(feedingInstances, statement);
            }
            connection.close();//Close DB connection
            return feedingInstances;
        }
    }

    private void getResults(List<FeedingInstance> feedingInstances, PreparedStatement statement) throws SQLException {
        //Execute the statement
    	try (ResultSet resultSet = statement.executeQuery()) {
    		//iterate through the result set
            while (resultSet.next()) {
            	//And create a new feeding instance
                FeedingInstance feedingInstance = new FeedingInstance(
                        resultSet.getInt("id")
                        ,resultSet.getDouble("amount_ml")
                        ,resultSet.getTimestamp("start_time").toLocalDateTime()
                        ,resultSet.getTimestamp("end_time").toLocalDateTime()
                );
                //And add it to the list
                feedingInstances.add(feedingInstance);
            }
        }
    }

    public List<FeedingInstance> getFeedingInstancesByTimePeriod(LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        try (Connection connection = createCon()) {//Create DB connection
            List<FeedingInstance> feedingInstances = new ArrayList<>();//Initialize feeding instances list
            //Create SQL query
            String sql = "SELECT id, amount_ml, start_time, end_time FROM feeding WHERE start_time >= ? AND end_time <= ? ORDER BY id";
            //Create the statement
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            	//Insert the data
                statement.setTimestamp(1, Timestamp.valueOf(startTime));
                statement.setTimestamp(2, Timestamp.valueOf(endTime));
                //Call the getResults method providing the list and the statement
                getResults(feedingInstances, statement);
            }
            connection.close();
            return feedingInstances;
        }
    }

    public double getAverageMilkConsumedPerSession() throws SQLException {
        try (Connection connection = createCon()) {//Create DB connection
        	//Create SQL query
        	//Use the AVG to get the average of all the rows for the amount_ml column
            String sql = "SELECT AVG(amount_ml) AS avg_milk_consumed FROM feeding";
            //Create the statement
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                //Execute the statement
            	try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {//Ensure there are results
                    	connection.close();//CLose DB
                        return resultSet.getDouble("avg_milk_consumed");
                    }
                }
            }
            connection.close();
            return 0.0;//Default value in case no results
        }
    }

    public Time getAverageFeedingSessionDuration() throws SQLException {
        try (Connection connection = createCon()) {//Create DB connection
        	//Create SQL query
        	//Use AVG while also subtracting end_time from start_time
        	//To get the average duration for all rows
            String sql = "SELECT AVG(end_time - start_time) AS avg_duration FROM feeding";
            //Create statement
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            	//Execute statement
                try (ResultSet resultSet = statement.executeQuery()) {
                	//Ensure there are results
                    if (resultSet.next()) {
                        return resultSet.getTime("avg_duration");
                    }
                }
            }
            connection.close();
            return null; 
        }
    }

    public double calculateAverageFeedingSessionDurationByTimePeriod(LocalDateTime start, LocalDateTime end) throws SQLException {
        double averageDuration = 0.0;

        // Call getFeedingInstancesByTimePeriod method by providing start and end time
        //And store the feedingInstances in a list
        List<FeedingInstance> feedingInstances = getFeedingInstancesByTimePeriod(start, end);


        long totalDuration = 0;
        
        // Calculate the total duration and count of feeding instances
        for (FeedingInstance feedingInstance : feedingInstances) {
            LocalDateTime startTime = feedingInstance.getStartTime();
            LocalDateTime endTime = feedingInstance.getEndTime();

            //Calculate the duration between the two times
            Duration duration = Duration.between(startTime, endTime);
            //And accumulate it
            totalDuration += duration.getSeconds();
        }

        // Calculate the average duration by diving totalDuration by the size of the list
        if (feedingInstances.size() > 0) {
            averageDuration = (double) totalDuration / feedingInstances.size();
        }
        System.out.println(averageDuration);
        //Divide by 60 to return the value in minutes
        return averageDuration/60;

    }

    public double calculateAverageMilkConsumedPerFeedingPerSession(LocalDateTime start, LocalDateTime end) throws SQLException {
        double averageMilkConsumed = 0.0;
        //Call getFeedingInstancesByTimePeriod method by providing start and end time
        //And store the feedingInstances in a list
        List<FeedingInstance> feedingInstances = getFeedingInstancesByTimePeriod(start, end);

        double totalMilk = 0;
        //Loop through the list
        for (FeedingInstance feedingInstance : feedingInstances) {
        	//Accumulate the milk consumed
        	totalMilk += feedingInstance.getAmountMl();;
        }

        if (feedingInstances.size() > 0) {//Make sure the list is not empty
        	//Calculate the average
            averageMilkConsumed = totalMilk / feedingInstances.size();
        }

        return averageMilkConsumed;
    }
}
