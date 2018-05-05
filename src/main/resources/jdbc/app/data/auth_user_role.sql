INSERT INTO app.auth_user_role (user_id, role_id)
  SELECT auth_user.user_id, auth_role.role_id
    FROM app.auth_user AS auth_user, app.auth_role AS auth_role
   WHERE auth_user.user_mail = 'testuser@domeet.cat'
     AND auth_role.role_code = 'test';

INSERT INTO app.auth_user_role (user_id, role_id)
  SELECT auth_user.user_id, auth_role.role_id
  FROM app.auth_user AS auth_user, app.auth_role AS auth_role
  WHERE auth_user.user_mail = 'adminuser@domeet.cat'
    AND auth_role.role_code = 'admin';

INSERT INTO app.auth_user_role (user_id, role_id)
  SELECT auth_user.user_id, auth_role.role_id
  FROM app.auth_user AS auth_user, app.auth_role AS auth_role
  WHERE auth_user.user_mail = 'appuser@domeet.cat'
    AND auth_role.role_code = 'regularuser';

INSERT INTO app.auth_user_role (user_id, role_id)
  SELECT auth_user.user_id, auth_role.role_id
  FROM app.auth_user AS auth_user, app.auth_role AS auth_role
  WHERE auth_user.user_mail LIKE 'demouser%@domeet.cat'
    AND auth_role.role_code = 'regularuser';