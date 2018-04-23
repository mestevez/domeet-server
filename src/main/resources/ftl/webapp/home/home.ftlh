<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">
	<meta name=viewport content="width=device-width,initial-scale=1">

	<title>${pagetitle}</title>

	<link href=/static/dist/css/home.css rel=stylesheet>
	<link href='https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons' rel="stylesheet">
</head>
<body>
<div id=home></div>
<script type=text/javascript>
	window.appData = {
		app: {
		},
		user: {
		<#list user?keys as ukey>
			${ukey} : <#if user[ukey]??>"${user[ukey]}"<#else>null</#if>,
		</#list>
		}, i18n: {
		<#list i18n?keys as ikey>
			${ikey} : "${i18n[ikey]}",
		</#list>
	}
	};
</script>
<script type=text/javascript src=/static/dist/js/manifest.js></script>
<script type=text/javascript src=/static/dist/js/vendor.js></script>
<script type=text/javascript src=/static/dist/js/home.js></script>
</body>
</html>