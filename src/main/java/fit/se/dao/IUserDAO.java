package fit.se.dao;

import fit.se.model.User;
import java.util.List;

public interface IUserDAO {
    boolean add(User user) throws Exception;
    boolean update(User user) throws Exception;
    boolean delete(int id) throws Exception;
    User findById(int id) throws Exception;
    User findByUsername(String username) throws Exception;
    List<User> findAll() throws Exception;
    List<User> findByRole(User.Role role) throws Exception;
    boolean updateLastLogin(String username) throws Exception;
    boolean changePassword(String username, String newPasswordHash) throws Exception;
}
