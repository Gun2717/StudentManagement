package fit.se.api;

import fit.se.model.Student;
import fit.se.service.StudentService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

/**
 * REST API for Student Management
 * Base URL: http://localhost:8080/api
 */
@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    private StudentService studentService;

    public StudentResource(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /api/students - Get all students
     */
    @GET
    public Response getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return Response.ok(students).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to fetch students: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/students/{id} - Get student by ID
     */
    @GET
    @Path("/{id}")
    public Response getStudentById(@PathParam("id") String id) {
        try {
            Student student = studentService.findStudentById(id);
            if (student == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Student not found: " + id))
                        .build();
            }
            return Response.ok(student).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/students - Create new student
     */
    @POST
    public Response createStudent(Student student) {
        try {
            boolean success = studentService.addStudent(student);
            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity(student)
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to create student"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * PUT /api/students/{id} - Update student
     */
    @PUT
    @Path("/{id}")
    public Response updateStudent(@PathParam("id") String id, Student student) {
        try {
            student.setId(id);
            boolean success = studentService.updateStudent(student);
            if (success) {
                return Response.ok(student).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Student not found: " + id))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * DELETE /api/students/{id} - Delete student
     */
    @DELETE
    @Path("/{id}")
    public Response deleteStudent(@PathParam("id") String id) {
        try {
            boolean success = studentService.deleteStudent(id);
            if (success) {
                return Response.noContent().build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Student not found: " + id))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/students/search?name=xxx - Search by name
     */
    @GET
    @Path("/search")
    public Response searchStudents(@QueryParam("name") String name,
                                   @QueryParam("major") String major,
                                   @QueryParam("minGpa") Double minGpa) {
        try {
            List<Student> results;

            if (name != null && !name.isEmpty()) {
                results = studentService.searchByName(name);
            } else if (major != null && !major.isEmpty()) {
                results = studentService.searchByMajor(major);
            } else if (minGpa != null) {
                results = studentService.getTopStudents(minGpa);
            } else {
                results = studentService.getAllStudents();
            }

            return Response.ok(results).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/students/statistics - Get statistics
     */
    @GET
    @Path("/statistics")
    public Response getStatistics() {
        try {
            StudentService.StudentStatistics stats = studentService.calculateStatistics();
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // Error response model
    public static class ErrorResponse {
        private String error;
        private long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
