import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceSet;

import java.io.File;

/**
 * Embedded Tomcat Server for SkillSync Backend
 * 
 * This class starts an embedded Tomcat server to serve the REST API
 * without requiring a separate servlet container installation.
 */
public class Server {
    
    private static final int PORT = 8080;
    private static final String WEB_APP_DIR = "web";
    private static final String CONTEXT_PATH = "";
    
    public static void main(String[] args) {
        System.out.println("=== Starting SkillSync Backend Server ===");
        
        try {
            // Create embedded Tomcat server
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(PORT);
            
            // Set up web app directory
            File webAppDir = new File(WEB_APP_DIR);
            if (!webAppDir.exists()) {
                System.out.println("Creating web app directory: " + webAppDir.getAbsolutePath());
                webAppDir.mkdirs();
            }
            
            // Add webapp
            Context ctx = tomcat.addWebapp(CONTEXT_PATH, webAppDir.getAbsolutePath());
            
            // Add compiled classes to classpath
            File additionWebInfClasses = new File("build/classes");
            WebResourceSet resources = tomcat.getResources().createCollection(
                WebResourceRoot.ResourceSetType.POST_RESOURCES, "/WEB-INF/classes", true, "/");
            resources.setRoot(additionWebInfClasses.getAbsolutePath());
            ctx.getResources().addWebResourceSet(resources);
            
            // Add JAR files to classpath
            File additionWebInfLib = new File("lib");
            if (additionWebInfLib.exists()) {
                resources = tomcat.getResources().createCollection(
                    WebResourceRoot.ResourceSetType.POST_RESOURCES, "/WEB-INF/lib", true, "/");
                resources.setRoot(additionWebInfLib.getAbsolutePath());
                ctx.getResources().addWebResourceSet(resources);
            }
            
            System.out.println("Server configured on port: " + PORT);
            System.out.println("Web app directory: " + webAppDir.getAbsolutePath());
            System.out.println("Context path: " + CONTEXT_PATH);
            
            // Start server
            tomcat.start();
            System.out.println("✓ SkillSync Backend Server started successfully!");
            System.out.println("API Base URL: http://localhost:" + PORT + "/api");
            System.out.println();
            System.out.println("Available endpoints:");
            System.out.println("  POST /api/auth/login - User login");
            System.out.println("  POST /api/auth/register - User registration");
            System.out.println("  POST /api/auth/logout - User logout");
            System.out.println("  GET  /api/auth/me - Get current user");
            System.out.println("  POST /api/requests - Send request");
            System.out.println("  GET  /api/requests/received/{userId} - Get received requests");
            System.out.println("  GET  /api/requests/sent/{userId} - Get sent requests");
            System.out.println("  PUT  /api/requests/{requestId}/accept - Accept request");
            System.out.println("  PUT  /api/requests/{requestId}/reject - Reject request");
            System.out.println("  PUT  /api/requests/{requestId}/complete - Complete request");
            System.out.println("  GET  /api/credits/{userId} - Get user credits");
            System.out.println("  GET  /api/credits/{userId}/history - Get credit history");
            System.out.println();
            System.out.println("Press Ctrl+C to stop the server");
            
            // Keep server running
            tomcat.getServer().await();
            
        } catch (LifecycleException e) {
            System.err.println("✗ Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
