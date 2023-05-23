package modal;

import java.sql.Connection;

import bean.Login_Bean;
import model.DB_Connection;

public class Login_Modal {
	
	public boolean check_user_name(Login_Bean obj_Login_Bean) {
		
		boolean flag = false;
		
		DB_Connection obj_DB_Connection = new DB_Connection();
		Connection connection = obj_DB_Connection.getConnection(); 
		
		return flag; 
		
	}

}
