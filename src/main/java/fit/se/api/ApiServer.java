package fit.se.api;

import fit.se.dao.*;
import fit.se.service.StudentService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * REST API Server using Jersey + Jetty
 */
public class ApiServer {
    private Server server;
    private static final int PORT = 8080;

    public ApiServer(StudentService studentService) {
        // Create Jersey resource config
        ResourceConfig config = new ResourceConfig();
        config.register(new StudentResource(studentService));
        config.register(JacksonFeature.class);
        config.register(CorsFilter.class);

        // Create Jetty server
        server = new Server(PORT);

        // Create servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add Jersey servlet
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));
        jerseyServlet.setInitOrder(0);
        context.addServlet(jerseyServlet, "/api/*");
    }

    public void start() throws Exception {
        server.start();
        System.out.println("🚀 REST API Server started at http://localhost:" + PORT + "/api");
        System.out.println("📖 API Documentation:");
        System.out.println("  GET    /api/students           - Get all students");
        System.out.println("  GET    /api/students/{id}      - Get student by ID");
        System.out.println("  POST   /api/students           - Create new student");
        System.out.println("  PUT    /api/students/{id}      - Update student");
        System.out.println("  DELETE /api/students/{id}      - Delete student");
        System.out.println("  GET    /api/students/search    - Search students");
        System.out.println("  GET    /api/students/statistics - Get statistics");
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
            System.out.println("🛑 REST API Server stopped");
        }
    }

    public void join() throws InterruptedException {
        server.join();
    }

    // CORS Filter for cross-origin requests
    @jakarta.ws.rs.ext.Provider
    public static class CorsFilter implements jakarta.ws.rs.container.ContainerResponseFilter {
        @Override
        public void filter(jakarta.ws.rs.container.ContainerRequestContext requestContext,
                           jakarta.ws.rs.container.ContainerResponseContext responseContext) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
            responseContext.getHeaders().add("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS");
            responseContext.getHeaders().add("Access-Control-Allow-Headers",
                    "Content-Type, Authorization");
        }
    }
}
