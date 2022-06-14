package com.revature.service;

import com.revature.dao.UserDao;
import com.revature.models.User;

public class UserService {
	
	private static final UserDao uDao = new UserDao();
	
	public static boolean createUser(User u) {
		if (uDao.existsByName(u.getUsername())) {
			return false;
		} else {
			uDao.insertUser(u);
			return true;
		}
	}

	public static User getUser(String username) {
		return uDao.getUserByUsername(username);
	}
}