package com.revature.proj1;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

class Plant {
    private int plantId;
    private String name;

    public Plant() {
    }
    public Plant(int plantId, String name) {
        this.plantId = plantId;
        this.name = name;
    }
    public int getPlantId() {
        return plantId;
    }
    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Plant [name=" + name + ", plantId=" + plantId + "]";
    }    
}

public class App {
    public static void main(String[] args) throws SQLException {
        //Database
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;INIT=runscript from 'classpath:schema.sql'", "sa", "");

        HttpServlet plantServlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                List<Plant> plants = new ArrayList<>();
                try {
                    ResultSet rs = connection.prepareStatement("select * from plants").executeQuery();
                    while(rs.next()) {
                        plants.add(new Plant(rs.getInt("PlantId"), rs.getString("Name")));
                    }
                } catch (SQLException e) {
                    System.err.println("Failed to retrive from db: " + e.getSQLState());
                }
                // JSON Mapper
                ObjectMapper mapper = new ObjectMapper();
                String results = mapper.writeValueAsString(plants);
                resp.setContentType("application/json");
                resp.getWriter().println(results);
            }
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                ObjectMapper mapper = new ObjectMapper();
                Plant newPlant = mapper.readValue(req.getInputStream(), Plant.class);
                System.out.println(newPlant);
                try {
                    PreparedStatement stmt = connection.prepareStatement("insert into 'plant' values (?, ?)");
                    stmt.setInt(1, newPlant.getPlantId());
                    stmt.setString(2, newPlant.getName());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Failed to insert: " + e.getMessage());
                }
            }
        };        

        //Server
        Tomcat server = new Tomcat();
        server.getConnector();
        server.addContext("", null);
        server.addServlet("", "defaultServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String filename = req.getPathInfo();
                String resourceDir = "static";
                InputStream file = getClass().getClassLoader().getResourceAsStream(resourceDir + filename);
                String mimeType = getServletContext().getMimeType(filename);
                resp.setContentType(mimeType);
                IOUtils.copy(file, resp.getOutputStream());
            }
        }).addMapping("/*");
        server.addServlet("", "plantsServlet", plantServlet).addMapping("/plants");
        try {
            server.start();
        } catch (LifecycleException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
