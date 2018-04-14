<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">

	<title>${pagetitle}</title>
</head>
<body>
	This are the application users:
	<table>
		<thead>
			<tr>
				<th>ID</th>
				<th>Email</th>
			</tr>
		</thead>
		<tbody>
		<#list usersList as userItem>
			<tr>
				<td>${userItem.getUserID()}</td>
				<td>${userItem.getUserMail()}</td>
			</tr>
		</#list>
		</tbody>
	</table>
</body>
</html>