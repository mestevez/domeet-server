package model;

import org.hibernate.Session;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( schema="app", name="auth_role" )
public class auth_role implements Serializable {

	@Id
	private Integer role_id;

	private String role_code;

	private String role_description;

	@ManyToMany(mappedBy = "user_roles")
	private Set<auth_user> users = new HashSet<>();

	public static auth_role getRole(Session session, String role_code) {
		auth_role role;

		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
		try {
			role =  entityManager.createQuery("SELECT a FROM auth_role a WHERE role_code = :role_code", auth_role.class)
					.setParameter("role_code", role_code)
					.getSingleResult();
		} finally {
			entityManager.close();
		}

		return role;
	}

	public Integer getRoleID() {
		return role_id;
	}

	public String getRoleCode() {
		return role_code;
	}
}
