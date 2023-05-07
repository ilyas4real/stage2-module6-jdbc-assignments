package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO users(firstName, lastName, age) VALUES (?,?,?)";
    private static final String updateUserSQL = "UPDATE users SET firstName=?, lastName=?, age=? WHERE id=?";
    private static final String deleteUser = "DELETE FROM users WHERE id=?";
    private static final String findUserByIdSQL = "SELECT * FROM users WHERE id=?";
    private static final String findUserByNameSQL = "SELECT * FROM users WHERE firstName=?";
    private static final String findAllUserSQL = "SELECT * FROM users";

    public Long createUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RuntimeException("Failed to retrieve generated ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public User findUserById(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByIdSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getLong("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getInt("age"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findUserByName(String userName) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByNameSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getLong("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getInt("age"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAllUser() {
        List<User> list = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findAllUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(updateUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
            return findUserById(user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteUser)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
