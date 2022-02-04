# Kerberos-Implementation

### GOALS
1) Understand the Algo, Messages being sent, Diffie Hellman exchange (we can use this for Private key exchange), Signing Process, encryption algo.
2) Starting servers/machines (in different ports) and exchanging messages after encrypting
3) Make the different functions (ENC, DEC, Diffie Hellman, Different elements/roles etc) and test locally
4) Install each pogram into different VMs and test out

### Phase 1:
* Do the exchange part, spin up computers establish a connection between them and send and recieve messages(TCP/UPD).
* Implement encryption algorithm (which can use the key) and the Diffie hellman exchange.

### Some variables:
* which enc algo does Kerberos use (Private-public key exchange and private key-DES)?
* how is the private keys actually exchanged (Diffie hellman itself?)
