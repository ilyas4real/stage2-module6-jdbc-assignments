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
    private static final String updateUserSQL = "UPDATE users SET firstName=?, lastName=?, age=?";
    private static final String deleteUser = "DELETE FROM users WHERE id=?";
    private static final String findUserByIdSQL = "SELECT * FROM users WHERE id=?";
    private static final String findUserByNameSQL = "SELECT * FROM users WHERE firstName=?";
    private static final String findAllUserSQL = "SELECT * FROM users";

    public Long createUser(User user){
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.execute();
            return user.getId();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public User findUserById(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4));
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public User findUserByName(String userName) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(2, userName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4));
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<User> findAllUser() {
        List<User> list = new ArrayList<>();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findAllUserSQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ResultSet rs = ps.executeQuery();
            ps.setLong(1, user.getId());
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            return findUserById(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void deleteUser(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
