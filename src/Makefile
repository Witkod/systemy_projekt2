compile:
	javac *.java && rmic TrafficCircleSimulation

server_tcs1:
	java -Djava.security.manager -Djava.security.policy=policy-file Server tcs1
	
server_tcs2:
	java -Djava.security.manager -Djava.security.policy=policy-file Server tcs2
	
server_tcs3:
	java -Djava.security.manager -Djava.security.policy=policy-file Server tcs3
	
server_tcs4:
	java -Djava.security.manager -Djava.security.policy=policy-file Server tcs4

client:
	java -Djava.security.manager -Djava.security.policy=policy-file Client

clean:

reset:
	rm *.class

.PHONY: all clean
