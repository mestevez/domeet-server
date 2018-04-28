<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">
	<meta name=viewport content="width=device-width,initial-scale=1">

	<title>${pagetitle}</title>

	<link href=/static/dist/css/account.css rel=stylesheet>
	<link href='https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons' rel="stylesheet">
</head>
<body>
<div id=account></div>
<script type=text/javascript>
	window.appData = {
		app: {
			user_email: '${user_email}',
			user_firstname: '${user_firstname}',
			user_lastname: <#if user_lastname??>"${user_lastname}"<#else>null</#if>,
			user_company: <#if user_company??>"${user_company}"<#else>null</#if>,
			user_phone: <#if user_phone??>"${user_phone}"<#else>null</#if>,
			user_photo: <#if user_photo??>"${user_photo}"<#else>null</#if>,
			user_schedule: <#if user_schedule??>"${user_schedule}"<#else>null</#if>,
			working_hours: [
				<#list working_hours as working_item>
					{ text: ${working_item.text}, value: ${working_item.value} },
				</#list>
			],
			navigation : {
			    back: '/'
			}
		},
		user: {
		<#list user?keys as ukey>
			${ukey} : <#if user[ukey]??>"${user[ukey]}"<#else>null</#if>,
		</#list>
		},
		i18n: {
		<#list i18n?keys as key>
			${key} : "${i18n[key]}",
		</#list>
		}
	};
</script>
<script type=text/javascript src=/static/dist/js/manifest.js></script>
<script type=text/javascript src=/static/dist/js/vendor.js></script>
<script type=text/javascript src=/static/dist/js/account.js></script>
</body>
</html>