package com.revature.dao;

import com.revature.MainDriver;
import com.revature.models.Account;
import com.revature.models.AccountType;
import com.revature.models.User;
import com.revature.util.ConnectionFactory;

import java.util.ArrayList;
import java.sql.*;

public class AccountDao implements AccountDaoInterface {

    /**
     * Inserts into the database a new account with specified primary_owner_id, accountType, and balance.
     *
     * @param account Primary and joint owner IDs will be ignored, as will approval status.
     * @param user Do not pass a user object without a valid userID from the database.
     */
    @Override
    public void insertAccount(Account account, User user) {
        MainDriver.monitor.getDbRequestLatency().record(() -> {
            //int accountID, int primaryOwnerID, int secondaryOwnerID, AccountType accountType, double balance, boolean approved
            String sql = "insert into accounts (primary_owner_id, joint_owner_id, account_type, balance, approved) values " +
                    "((select user_id from users where username = ?),?,?::a_type,?,?)";
            Connection connection = ConnectionFactory.getConnection();

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setInt(2, account.getJointOwnerID());
                ps.setString(3, account.getAccountType().name());
                ps.setDouble(4, account.getBalance());
                ps.setBoolean(5, false);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the joint_owner_id, balance, and approved columns for a given accountID.
     * @param account Do not pass an account without a valid accountID from the database.
     */
    @Override
    public void updateAccount(Account account) {
        MainDriver.monitor.getDbRequestLatency().record(() -> {
            String sql = "update accounts set joint_owner_id = ?, balance = ?, approved = ? where account_id = ?";
            Connection connection = ConnectionFactory.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, account.getJointOwnerID());
                ps.setDouble(2, account.getBalance());
                ps.setBoolean(3, account.isApproved());
                ps.setInt(4, account.getAccountID());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Queries the database and returns an account with the specified accountID.
     * @param accountID Do not pass an invalid accountID.
     * @return an account with the specified accountID.
     */
    @Override
    public Account getAccountByAccountID(int accountID) {
        return MainDriver.monitor.getDbRequestLatency().record(() -> {
            String sql = "select * from accounts where account_id = ?";
            Connection connection = ConnectionFactory.getConnection();
            Account account = null;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, accountID);

                ResultSet rs = ps.executeQuery();
                //int accountID, int primaryOwnerID, int secondaryOwnerID, AccountType accountType, double balance, boolean approved
                if (rs.next()) {
                    account = new Account(
                            rs.getInt("account_id"),
                            rs.getInt("primary_owner_id"),
                            rs.getInt("joint_owner_id"),
                            AccountType.valueOf(rs.getString("account_type")),
                            rs.getDouble("balance"),
                            rs.getBoolean("approved")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return account;
        });
    }

    /**
     * Queries the database and returns a list of accounts for the specified ownerID, whether primary or joint.
     * @param ownerID Do not pass an invalid ownerID.
     * @return a list of accounts with the specified owner.
     */
    @Override
    public ArrayList<Account> getAccountsByOwnerID(int ownerID) {
        return MainDriver.monitor.getDbRequestLatency().record(() -> {
            String sql = "select * from accounts where primary_owner_id = ? or joint_owner_id = ?";
            Connection connection = ConnectionFactory.getConnection();
            ArrayList<Account> accounts = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, ownerID);
                ps.setInt(2, ownerID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getInt("primary_owner_id"),
                            rs.getInt("joint_owner_id"),
                            AccountType.valueOf(rs.getString("account_type")),
                            rs.getDouble("balance"),
                            rs.getBoolean("approved")
                    );
                    accounts.add(account);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return accounts;
        });
    }

    /**
     * Queries the database and returns a list of accounts with the specified approvalStatus.
     * @param approvalStatus
     * @return  a list of accounts with the specified approvalStatus.
     */
    @Override
    public ArrayList<Account> getAccountsByApproval(boolean approvalStatus) {
        return MainDriver.monitor.getDbRequestLatency().record(() -> {
            String sql = "select * from accounts where approved = ?";
            Connection connection = ConnectionFactory.getConnection();
            ArrayList<Account> accounts = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setBoolean(1, approvalStatus);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getInt("primary_owner_id"),
                            rs.getInt("joint_owner_id"),
                            AccountType.valueOf(rs.getString("account_type")),
                            rs.getDouble("balance"),
                            rs.getBoolean("approved")
                    );
                    accounts.add(account);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return accounts;
        });
    }
}
