// Info for Groovy logback:
// https://logback.qos.ch/manual/groovy.html

//loggingLevel : 'ALL'
//consoleLogEnabled : true
//logFileName : 'test.log'
//logDir : 'logs'

root(ALL, ["STDOUT"])
println "Logback Groovy configuration. loggingLevel: ${loggingLevel}"