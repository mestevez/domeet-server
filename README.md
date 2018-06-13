<h1>DoMeet-Server</h1> 

<h2>System configuration</h2>
<h3>Requirements</h3>
<p>DoMeet server can be installed on any operating system (Linux, Windows, Mac OS X,) since it is a multi-platform application.</p>
<p>It is required to have some applications installed in order to run the server.</p>
<ul>
	<li>Java 8 (also called Java 1.8)</li>
	<li>NPM (v.5.6.0 or greater)</li>
	<li>PostgresSQL (v.9.6 or greater)</li>
</ul>
<h3>Java 8</h3>
<h4>Checking the Java version:</h4>
<ol>
	<li>
		<p>Open a terminal:</p>
		<p>On Linux or Mac OS X: open a terminal.</p>
		<p>On Windows: press "windows" key + r, type cmd (or command) in the Run window and press "OK" or open the "Prompt command" from "Start > Programs > Accessories" menu.</p>
	</li>
	<li>
		<p>Type java -version and press Enter.</p>
		<pre>java -version</pre>
	</li>
</ol>

<p>If Java is correctly installed on your computer, the name and version of the Java virtual machine is displayed:</p>

<pre>
Java version "1.8.0_31"
Java(TM) SE Runtime Environment (build 1.8.0_31-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.31-b07, mixed mode)
</pre>

<p>If Java is not installed on your computer, it will fail to display the Java version. Then, you will need to install Java (see below).</p>

<p>If Java is installed but not included in the PATH, it will fail to find the Java command. Then, you will need to add $JAVA_HOME/bin/ in your PATH (see: <a href="http://www.java.com/en/download/help/path.xml">How do I set or change the PATH system variable?</a>).</p>

<p>Java packages and instructions for installation are available from the Oracle website:</p>
<ul>
	<li><a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html">Download</li>
	<li><a href="https://docs.oracle.com/javase/8/docs/technotes/guides/install/">Instructions</li>
</ul>

<h3>NPM</h3>
<h4>Checking the NPM version:</h4>
<ol>
	<li>Open a terminal:</li>
	<li>
		<p>Type npm -version and press Enter.</p>
		<pre>npm -version</pre>
	</li>
</ol>

<p>If NPM is correctly installed on your computer, the name and version of the NPM is displayed:</p>

<pre>
5.6.0
</pre>

<p>If NPM is not installed on your computer, it will fail to display the NPM 
version. Then, you will need to install NPM 
(check out the <a href="https://www.npmjs.com/get-npm">official documentation</a>).</p>

<h3>PostgreSQL</h3>
<p>If PostgreSQL is not installed on your computer, check out the <a href="https://www.postgresql.org/">official documentation</a>).</p>

<mark>PostgreSQL must be installed with UTF-8 encoding.</mark>

<h2>Installation</h2>

<h3>Configuration file</h3>

<h4>Database configuration</h4>
<p>
	The application requires connecting to a PostgreSQL database, so some database configuration
	 is required. By default it connects to a postgreSQL 
	server at <code>localhost</code> listening by the port <code>5432</code>. 
	And connecting with the administrative user <code>postgres</code> with password
	<code>12345</code>. This configuration can be modified in the mentioned configuration file. 
</p>

<h4>SMTP configuration file</h4>
<p>
As the application is intended to send emails, it requires some STMP 
configuration to execute properly.
</p>

<p>
	The application configuration is mainly defined by the <b>configuration file</b> <em>conf/domeet.propertes</em>:
</p>
<pre>dbms_host = localhost
dbms_port = 5432
dbms_name = domeet
dbms_user = postgres
dbms_password = 12345
smtp_host = smtp.gmail.com
smtp_port = 587
smtp_user = domeet.uoc@gmail.com
smtp_password = 0q4prpb3n0xtd4cs
http_port = 8081</pre>

<p>
	The configuration file contains every necessary to set up the application:
	<table>
		<thead>
			<tr>
				<th>Area</th>
				<th>Property</th>
				<th>Default value</th>
				<th>Description</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td rowspan='5'>Database</td>
				<td><b>dbms_host</b></td>
				<td>localhost</td>
				<td>Database endpoint. URL identifying the database system manager instance.</td>
			</tr>
			<tr>
				<td><b>dbms_port</b></td>
				<td>5432</td>
				<td>Port throught the database system manager instance handle communications.</td>
			</tr>
			<tr>
				<td><b>dbms_name</b></td>
				<td>domeet</td>
				<td>Name of the database holding enterprise information.</td>
			</tr>
			<tr>
				<td><b>dbms_user</b></td>
				<td>postgres</td>
				<td>
					User ID for connection to the database. 
					The user requries administrative permissons over the specifyed database for handling 
					the database model.
				</td>
			</tr>
			<tr>
				<td><b>dbms_password</b></td>
				<td>12345</td>
				<td>Credential password related for the defined database user.</td>
			</tr>
			<tr>
				<td rowspan='4'>SMTP</td>
				<td><b>smtp_host</b></td>
				<td>smtp.gmail.com</td>
				<td>SMTP host URL</td>
			</tr>
			<tr>
				<td><b>smtp_port</b></td>
				<td>587</td>
				<td>Port throught the SMTP host handles communications.</td>
			</tr>
			<tr>
				<td><b>smtp_user</b></td>
				<td>domeet.uoc@gmail.com</td>
				<td>E-Mail account used for sending emails via the application.</td>
			</tr>
			<tr>
				<td><b>smtp_password</b></td>
				<td>0q4prpb3n0xtd4cs</td>
				<td>Credential password related for the defined E-Mail account.</td>
			</tr>
			<tr>
				<td>HTTP</td>
				<td><b>http_port</b></td>
				<td>8081</td>
				<td>HTTP Port for the application server</td>
			</tr>
		</tbody>
	</table>
</p>

<h3>Create application and start server</h3>
<ol>
	<li>Open a terminal and go to the root directory of the project</li>
	<li>
		<p>On Linux or Mac OS X type:<pre>./gradlew appRun</pre></p>
  	<p>On Windows type: <pre>gradlew.bat appRun</pre></p>
		<p>After a few minutes, it should return something like:</p>
		<pre><===========--> 91% EXECUTING [6m 7s]<br/><!--
         -->> :appRun<br/><!--
         <!--> IDLE
		</pre>  	
	</li>	
</ol>
 
 <p>Once the server is installed you can access it with the URL <a href="http://localhost:8081">http://localhost:8081</a></p>
<h2>Maintanance</h2>
 
<h4>Update configuration</h4>
<pre>gradlew loadconf</pre 
<h4>create database</h4>
<pre>gradlew database -Pcreate</pre>
<h4>drop database</h4>
<pre>gradlew database -Pdrop</pre>
<h4>load database data</h4>
<pre>gradlew database -Pload</pre>
<h4>check database status</h4>
<pre>gradlew database -Pstatus</pre>
