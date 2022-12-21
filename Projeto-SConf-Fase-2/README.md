Trabalho 1 - fase 2 de segurança e confiablidade
Grupo 036

- Hugo Consciência 54389
- Marco Rodrigues 55425
- Thiago Duarte 53636

Como correr o programa:

- Executar o TrokosServer port password-cifra keystore password-keystore

Obs.: O porto é opcional (por omissão, utiliza-se 45678).

Exemplo (c/ porto): 12345 cifraPass123 server.keystore 123456
Exemplo (s/ porto): cifraPass123 server.keystore 123456

- Executar o Trokos serverAddress truststore keystore password-keystore userID

Obs.: <serverAddress> = IP/hostname[:Port], onde o porto é opcional (por omissão, utiliza-se 45678).

* Exemplo para executar usuário 1 (c/ porto): localhost:12345 client.truststore user1.keystore password1 user1
* Exemplo para executar usuário 1 (s/ porto): localhost client.truststore user1.keystore password1 user1

* Exemplo para executar usuário 2 (c/ porto): localhost:12345 client.truststore user2.keystore password2 user2
* Exemplo para executar usuário 2 (s/ porto): localhost client.truststore user2.keystore password2 user2

* Exemplo para executar usuário 3 (c/ porto): localhost:12345 client.truststore user3.keystore password3 user3
* Exemplo para executar usuário 3 (s/ porto): localhost client.truststore user3.keystore password3 user3

Informações dos certificados e keystores:
* server.keystore		password: 123456
* server.truststore       password: 123456
* client.truststore	password: 123456
* user1.keystore		password: password1
* user2.keystore		password: password2
* user3.keystore		password: password3

Para mais informações, ler enunciado.pdf
