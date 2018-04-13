package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( schema="app", name="auth_user" )
public class auth_user {

	@Id
	private Integer user_id;

	private String user_mail;

	public String getUser_mail() {
		return user_mail;
	}
}
