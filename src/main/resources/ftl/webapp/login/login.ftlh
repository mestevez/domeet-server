<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">
	<meta name=viewport content="width=device-width,initial-scale=1">

	<title>${pagetitle}</title>

	<link href=/static/dist/css/login.css rel=stylesheet>
	<link href='https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons' rel="stylesheet">
</head>
<body>
<div id=login></div>
<script type=text/javascript>
	window.loginappData = {
		app: {
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
<script type=text/javascript src=/static/dist/js/login.js></script>
</body>
</html>