<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">
	<meta http-equiv="CONTENT-TYPE" CONTENT='text/html;charset=${charset}'>
	<meta name=viewport content="width=device-width,initial-scale=1">

	<title>${pagetitle}</title>

	<link href='https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons' rel="stylesheet">
	<link href=/static/dist/css/${appcss} rel=stylesheet>
</head>
<body>
<div id=${appid}></div>
<script type=text/javascript>
<#outputformat "JSON">
window.appData = ${appData};
</#outputformat>
</script>
<script type=text/javascript src=/static/dist/js/manifest.js></script>
<script type=text/javascript src=/static/dist/js/vendor.js></script>
<script type=text/javascript src=/static/dist/js/${appjs}></script>
</body>
</html>