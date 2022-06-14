package com.revature.dao;

import com.revature.models.Account;
import com.revature.models.AccountType;
import com.revature.models.User;
import com.revature.models.TransactionStatus;
import com.revature.util.ConnectionFactory;

import java.util.ArrayList;
import java.sql.*;

public class AccountDao implements AccountDaoInterface {

    /**
     * Inserts into the database a new account with specified primary_owner_id, accountType, and balance.
     *
     * @param account MUST NOT ALREADY EXIST IN THE DATABSE. primary and joint owner IDs will be ignored, as will approval status.
     * @param user MUST ALREADY EXIST IN THE DATABASE. Do not pass a user object without a valid userID from the database.
     */
    @Override
    public void insertAccount(Account account, User user) {
        //int accountID, int primaryOwnerID, int secondaryOwnerID, AccountType accountType, double balance, boolean approved
        String sql = "insert into accounts (primary_owner_id, account_type, balance, approved) values " +
                "((select user_id from users where username = ?),?,?,?)";
        Connection connection = ConnectionFactory.getConnection();

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, user.getUsername());
            ps.setString(2, account.getAccountType().name());
            ps.setDouble(3, account.getBalance());
            ps.setBoolean(4, false);
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateAccount(Account account) {
        String sql = "update accounts set joint_owner_id = ?, balance = ?, approved = ? where account_id = ?";
        Connection connection = ConnectionFactory.getConnection();

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1,account.getJointOwnerID());
            ps.setDouble(2, account.getBalance());
            ps.setBoolean(3, account.isApproved());
            ps.setInt(4, account.getAccountID());
            ps.execute();
        } catch (SQLException e){
            //e.printStackTrace();
        }
    }

    @Override
    public Account getAccountByAccountID(int accountID) {
        String sql = "select * from accounts where account_id = ?";
        Connection connection = ConnectionFactory.getConnection();
        Account account = null;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1,accountID);

            ResultSet rs = ps.executeQuery();
            //int accountID, int primaryOwnerID, int secondaryOwnerID, AccountType accountType, double balance, boolean approved
            if(rs.next()){
                account = new Account(
                        rs.getInt("account_id"),
                        rs.getInt("primary_owner_id"),
                        rs.getInt("joint_owner_id"),
                        AccountType.valueOf(rs.getString("account_type")),
                        rs.getDouble("balance"),
                        rs.getBoolean("approved")
                );
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public ArrayList<Account> getAccountsByOwnerID(int ownerID) {
        String sql = "select * from accounts where primary_owner_id = ? or joint_owner_id = ?";
        Connection connection = ConnectionFactory.getConnection();
        ArrayList<Account> accounts = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1,ownerID);
            ps.setInt(2, ownerID);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
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
        } catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public ArrayList<Account> getAccountsByApproval(boolean approvalStatus) {
        String sql = "select * from accounts where approved = ?";
        Connection connection = ConnectionFactory.getConnection();
        ArrayList<Account> accounts = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBoolean(1,approvalStatus);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
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
        } catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }
}
