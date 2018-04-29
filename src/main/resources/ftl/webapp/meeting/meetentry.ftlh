<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">
	<meta name=viewport content="width=device-width,initial-scale=1">

	<title>${pagetitle}</title>

	<link href=/static/dist/css/meetentry.css rel=stylesheet>
	<link href='https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons' rel="stylesheet">
</head>
<body>
<div id=meetentry></div>
<script type=text/javascript>
	window.appData = {
		app: {
			meet_type: 0,
			meeting_types: [
				{ text: 'Undetermined', value: 0 },
				{ text: 'Inform', value: 1 },
				{ text: 'Consult', value: 2 },
				{ text: 'Issues solving', value: 3 },
				{ text: 'Decision making', value: 4 }
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
<script type=text/javascript src=/static/dist/js/meetentry.js></script>
</body>
</html>