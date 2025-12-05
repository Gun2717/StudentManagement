package fit.se.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User model for authentication
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public enum Role {
        ADMIN("Quản trị viên", new String[]{"*"}),
        TEACHER("Giáo viên", new String[]{"view_student", "edit_student", "view_grade", "edit_grade"}),
        STUDENT("Sinh viên", new String[]{"view_own_info", "view_own_grade"});

        private final String displayName;
        private final String[] permissions;

        Role(String displayName, String[] permissions) {
            this.displayName = displayName;
            this.permissions = permissions;
        }

        public String getDisplayName() { return displayName; }
        public String[] getPermissions() { return permissions; }

        public boolean hasPermission(String permission) {
            if (permissions[0].equals("*")) return true;
            for (String p : permissions) {
                if (p.equals(permission)) return true;
            }
            return false;
        }
    }

    // Constructors
    public User() {}

    public User(String username, String passwordHash, String fullName, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean hasPermission(String permission) {
        return role != null && role.hasPermission(permission);
    }

    @Override
    public String toString() {
        return String.format("User[%s - %s - %s]", username, fullName, role.getDisplayName());
    }
}
