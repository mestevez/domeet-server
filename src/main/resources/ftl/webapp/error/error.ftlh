<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="${charset}">
	<meta name=viewport content="width=device-width,initial-scale=1">

	<title>${pagetitle}</title>

	<link href=/static/dist/css/error.css rel=stylesheet>
	<link href='https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons' rel="stylesheet">
</head>
<body>
<div id=errapp></div>
<script type=text/javascript>
	var errappData = {
	    app: {
			status_code		: ${status_code},
			error_message	: '${error_message}',
			error_trace		: '${error_trace}'
		},
		i18n: {
			btn_back: 'Back',
			label_error: 'Error'
		}
	};
</script>
<script type=text/javascript src=/static/dist/js/manifest.js></script>
<script type=text/javascript src=/static/dist/js/vendor.js></script>
<script type=text/javascript src=/static/dist/js/error.js></script>
</body>
</html>