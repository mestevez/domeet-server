INSERT INTO domeet.auth_user_role (user_id, role_id)
  SELECT auth_user.user_id, auth_role.role_id
    FROM domeet.auth_user AS auth_user, domeet.auth_role AS auth_role
   WHERE auth_user.user_mail = 'testuser@domeet.cat'
     AND auth_role.role_code = 'test';

INSERT INTO domeet.auth_user_role (user_id, role_id)
  SELECT auth_user.user_id, auth_role.role_id
  FROM domeet.auth_user AS auth_user, domeet.auth_role AS auth_role
  WHERE auth_user.user_mail = 'adminuser@domeet.cat'
        AND auth_role.role_code = 'admin';