# Tech Challenge 03 - Healthcare Appointment System

Este repositório contém a solução do Tech Challenge Fase 3: backend modular para agendamento de consultas, histórico médico e notificações assíncronas em ambiente hospitalar.

O projeto foi implementado com dois microsserviços Spring Boot, autenticação/autorização com Spring Security, GraphQL para histórico e mensageria RabbitMQ.

Tecnologias principais:

- Java 21
- Spring Boot 3.3.0
- Spring Security (Basic Auth)
- Spring Data JPA / Hibernate
- GraphQL (Spring for GraphQL)
- RabbitMQ
- MySQL 8
- Docker / Docker Compose

---

## Pré-requisitos

Antes de iniciar, verifique se possui:

- Docker
- Docker Compose

Comandos úteis:

```bash
docker version
docker compose version
```

---

## Como executar via Docker Compose

A partir da raiz do projeto:

```bash
docker compose up --build
```

O Docker Compose sobe:

- MySQL
- RabbitMQ
- Appointment Service
- Notification Service

Para parar:

```bash
docker compose down
```

Para recriar banco e filas do zero:

```bash
docker compose down -v
docker compose up --build
```

---

## Endereços dos serviços

- Appointment Service: `http://localhost:8081`
- Notification Service: `http://localhost:8082`
- GraphQL endpoint: `http://localhost:8081/graphql`
- RabbitMQ Management: `http://localhost:15672` (`guest` / `guest`)
- MySQL: `localhost:3306`

---

## Banco de dados

Script de inicialização:

```text
init-db.sql
```

Esse script cria as tabelas principais e popula dados iniciais para teste (usuários, consultas e histórico).

Usuários padrão:

- `doctor1 / password`
- `nurse1 / password`
- `patient1 / password`

---

## Segurança e regras de acesso

Autenticação HTTP Basic no Appointment Service.

Perfis:

- `DOCTOR`: cria, edita e visualiza consultas/histórico
- `NURSE`: cria, edita e visualiza consultas/histórico
- `PATIENT`: visualiza apenas as próprias consultas

Restrições validadas para PATIENT:

- `GET /api/appointments/{id}`
- `GET /api/appointments/patient/{patientId}`
- `GET /api/appointments/patient/{patientId}/future`

Pacientes não acessam dados de outros pacientes (`403 Forbidden`).

---

## Endpoints REST

Base URL (Appointment Service):

```text
http://localhost:8081
```

Recursos:

- `POST /api/appointments`
- `PUT /api/appointments/{id}`
- `GET /api/appointments/{id}`
- `GET /api/appointments/patient/{patientId}`
- `GET /api/appointments/patient/{patientId}/future`
- `GET /api/appointments/doctor/{doctorId}`

Notification Service:

- `POST /api/notifications/{id}/send`
- `GET /api/notifications/patient/{patientId}`

---

## GraphQL (Histórico do Paciente)

Endpoint:

```text
http://localhost:8081/graphql
```

Queries disponíveis:

- `getPatientMedicalHistory(patientId: ID!)`
- `getMedicalHistory(id: ID!)`

Exemplo:

```graphql
query {
  getPatientMedicalHistory(patientId: "3") {
    id
    diagnosis
    treatment
    medications
  }
}
```

---

## Comunicação assíncrona

Fluxo implementado:

1. Appointment Service cria/atualiza consulta
2. Evento é publicado no RabbitMQ
3. Notification Service consome a mensagem
4. Notificação é registrada no banco e processada

---

## Collection Postman

Arquivo:

```text
postman-collection.json
```

Importação:

1. Abra o Postman
2. Clique em `Import`
3. Selecione `postman-collection.json`

Observação: a collection deve usar `http://localhost:8081` para endpoints do Appointment Service.

---

## Testes unitários e cobertura

O repositório possui testes unitários para os serviços principais e para o listener de mensagens.

Para executar:

```bash
docker compose run --rm appointment-service mvn test -f appointment-service/pom.xml
docker compose run --rm notification-service mvn test -f notification-service/pom.xml
```

Para gerar relatório de cobertura (JaCoCo):

```bash
docker compose run --rm appointment-service mvn verify -f appointment-service/pom.xml
docker compose run --rm notification-service mvn verify -f notification-service/pom.xml
```

Relatórios:

```text
appointment-service/target/site/jacoco/index.html
notification-service/target/site/jacoco/index.html
```