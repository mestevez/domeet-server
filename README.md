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
<h3>Database configuration file</h3>
<p>
	The application is configured by default to connect to a postgres 
	server at <code>localhost</code> listening by the port <code>5432</code>. 
	And connecting with the administrative user <code>postgres</code> with password
	<code>12345</code>. This configuration can be modified in the file 
	<em>conf/security/jetty-realm-dbms.properties.</em> 
</p>
<ol>
	<li>Open configuration file: <pre>vi conf/security/jetty-realm-dbms.properties</pre></li>
	<li>
		Which contains:
		<pre>jdbcdriver = org.postgresql.Driver
url = jdbc:postgresql://<b>localhost</b>:<b>5432</b>/domeet
<b>username</b> = <b>postgres</b>
<b>password</b> = <b>12345</b>
...</pre>
	Only the parameters <code>url</code>, <code>username</code> and <code>password</code> can be edited on this file. 
	Otherwise the application server could not behave as expected.
	</li>
</ol>

<h3>SMTP configuration file</h3>
<p>
As the application is intended to send emails, it requires some STMP 
configuration to execute properly.  This configuration can be modified in the file
<em>conf/mail-config.</em> 
</p>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
	<entry key="mail.smtp.host">smtp.gmail.com</entry>
	<entry key="mail.smtp.port">587</entry>
	<entry key="mail.smtp.starttls.enable">true</entry>
	<entry key="mail.smtp.auth">true</entry>
	<entry key="user">domeet.uoc@gmail.com</entry>
	<entry key="password">0q4prpb3n0xtd4cs</entry>
</properties>
```

So, it must be given a valid STMP configuration. 

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
 
<h4>create database</h4>
<pre>gradlew database -Pcreate</pre>
<h4>drop database</h4>
<pre>gradlew database -Pdrop</pre>
<h4>load database data</h4>
<pre>gradlew database -Pload</pre>
<h4>check database status</h4>
<pre>gradlew database -Pstatus</pre>
