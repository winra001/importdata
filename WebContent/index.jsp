<%-- <%@page contentType="text/html" pageEncoding="UTF-8"%> --%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Read Excel</title>
<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
<style type="text/css">
	div {
		margin: 10px;
	}
	label {
		width: 100px;
		display: inline-block;
	}
</style>
</head>

<body>
	<div>
		<h3>Choose File to Upload in Server</h3>
		<form action="upload" method="post" enctype="multipart/form-data">
			<div><input type="file" name="file" /></div>
			<div>
				<label>File type :</label>
				<input type="radio" name="filetype" value="program">Program</input>
				<input type="radio" name="filetype" value="speaker">Speaker</input>
				<input type="radio" name="filetype" value="abstracts">Abstracts</input>
			</div>
			<div><label>First name :</label><input type="text" name="firstname" /> (Only if file type is Abstracts)</div>
			<div><label>Last name :</label><input type="text" name="lastname" /> (Only if file type is Abstracts)</div>
			<div><label>File path :</label><input type="text" name="filepath" value="C:\Development" /></div>
			<div><label>Url :</label><input type="text" name="url" value="jdbc:mysql://localhost/" /></div>
			<div><label>Database :</label><input type="text" name="database" /></div>
			<div><label>ID :</label><input type="text" name="id" /></div>
			<div><label>Password :</label><input type="password" name="password" /></div>
			<div style="margin-top: 40px;"><input type="submit" value="upload" /></div>
		</form>
	</div>
</body>
</html>