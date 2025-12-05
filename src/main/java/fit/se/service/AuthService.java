package fit.se.service;

import fit.se.dao.IUserDAO;
import fit.se.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthService {
    private IUserDAO userDAO;
    private BCryptPasswordEncoder passwordEncoder;
    private User currentUser;

    public AuthService(IUserDAO userDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Authenticate user with username and password
     */
    public boolean login(String username, String password) throws Exception {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            return false;
        }

        if (!user.isActive()) {
            throw new IllegalStateException("Tài khoản đã bị khóa");
        }

        // Verify password
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            currentUser = user;
            userDAO.updateLastLogin(username);
            return true;
        }

        return false;
    }

    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Register new user
     */
    public boolean register(String username, String password, String fullName,
                            String email, User.Role role) throws Exception {
        // Check if username exists
        if (userDAO.findByUsername(username) != null) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        // Hash password
        String passwordHash = passwordEncoder.encode(password);

        User user = new User(username, passwordHash, fullName, role);
        user.setEmail(email);

        return userDAO.add(user);
    }

    /**
     * Change password
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) throws Exception {
        User user = userDAO.findByUsername(username);

        if (user == null || !passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return false;
        }

        String newPasswordHash = passwordEncoder.encode(newPassword);
        return userDAO.changePassword(username, newPasswordHash);
    }

    /**
     * Check if user has permission
     */
    public boolean hasPermission(String permission) {
        return currentUser != null && currentUser.hasPermission(permission);
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }
}
