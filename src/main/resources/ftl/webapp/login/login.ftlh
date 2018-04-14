<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">

	<title>${pagetitle}</title>
</head>
<body>
<form method="POST" action="j_security_check">
	<input name="j_username" type="text" placeholder="User name" required autofocus>
	<input name="j_password" type="password" placeholder="Password" required>
	<button type="submit">Sign in</button>
</form>
</body>
</html>