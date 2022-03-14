# Implementation of Kerberos Authentication Protocol

### Goals

1) Understand the Protocol/Algorithm
2) Under security of the Protocol/Algorithm
3) Under the message formats used in the Protocol/Algorithm
4) Implement the Algorithm using the message formats referring RFC Specification for the Protocol.
5) Run the Protocol in different Virtual Machines
6) Test for Vulnerabilities and Security Issues (Basic Attacks)


### Understanding Kerberos Authentication Protocol

The following figure shows the basic workflow of Kerberos Authentication Protocol.

![Kerberos Authentication Workflow and Message Formats](https://github.com/gokulsreekumar/Kerberos-Implementation/blob/main/KerberosMessages.png)

**Two** part process for availing a service: 
- firstly, Authenication with KDC and receiving Tickets
- secondly, availing the service by using the Tickets

Messages are exchanged by Client with **three** other entities:
1. Authentication Server(AS)
2. Ticket Granting Server(TGS)
3. Service Server (S)
