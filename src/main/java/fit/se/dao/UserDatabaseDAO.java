package fit.se.dao;

import fit.se.model.User;
import fit.se.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabaseDAO implements IUserDAO {

    private Connection getConnection() throws SQLException, IOException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean add(User user) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole().name());
            pstmt.setBoolean(6, user.isActive());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(User user) throws Exception {
        String sql = "UPDATE users SET full_name=?, email=?, role=?, active=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setInt(5, user.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public User findById(int id) throws Exception {
        String sql = "SELECT * FROM users WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws Exception {
        String sql = "SELECT * FROM users ORDER BY username";
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }
        return users;
    }

    @Override
    public List<User> findByRole(User.Role role) throws Exception {
        String sql = "SELECT * FROM users WHERE role=? ORDER BY username";
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUser(rs));
                }
            }
        }
        return users;
    }

    @Override
    public boolean updateLastLogin(String username) throws Exception {
        String sql = "UPDATE users SET last_login=CURRENT_TIMESTAMP WHERE username=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean changePassword(String username, String newPasswordHash) throws Exception {
        String sql = "UPDATE users SET password_hash=? WHERE username=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        }
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }
}
