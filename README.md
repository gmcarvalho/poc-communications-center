# Central de Comunicações Multicanal

POC em Java 21 + Spring Boot 3.3.4 que ilustra uma arquitetura para orquestração de comunicações multicanal
com disparos imediatos e rotinas agendadas (D+N).

## Visão Geral

A aplicação expõe uma API REST para recebimento de eventos de comunicação, além de manter
um agendador diário que consulta comunicações armazenadas em um banco em memória (H2).
Ao receber um evento é possível:

* Executar disparos imediatos para e-mail, SMS, WhatsApp ou push notification.
* Registrar rotinas futuras por regra de negócio (ex.: lembrete de emissão de cartão) em múltiplos canais.
* Avaliar regras no momento da execução agendada (ex.: não reenviar comunicação caso o cartão já tenha sido emitido).
* Simular o fluxo completo de aprovação de KYC com disparo inicial em push e lembretes automáticos em D+15 e D+30 por e-mail.

## Execução

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8080` e o console do H2 em `http://localhost:8080/h2-console`.
Use as credenciais `sa/sa` e a URL `jdbc:h2:mem:communicationsdb`.

## Endpoints principais

| Método | Caminho | Descrição |
| --- | --- | --- |
| `POST` | `/api/communications/triggers` | Recebe um evento de comunicação com canais, regra e rotina opcional. |
| `GET` | `/api/communications/scheduled` | Lista comunicações registradas (pendentes, enviadas ou ignoradas). |
| `PUT` | `/api/customers/{customerId}/card-status` | Atualiza o status de emissão do cartão para avaliação de regras. |
| `GET` | `/api/customers/{customerId}/card-status` | Consulta se o cartão já foi emitido. |
| `POST` | `/api/kyc/validate` | Simula a aprovação de KYC, dispara push imediato e agenda lembretes D+15/D+30 por e-mail. |

## Fluxo completo de KYC

1. **Chame o endpoint de validação de KYC**

   ```http
   POST /api/kyc/validate
   Content-Type: application/json

   {
     "customerId": "12345",
     "customerName": "Maria Andrade",
     "email": "maria.andrade@example.com"
   }
   ```

   A resposta conterá os IDs das comunicações agendadas para D+15 e D+30, além da descrição
   do push disparado imediatamente.

2. **Observe os logs**: um push é enviado via `FakePushNotificationService` com a confirmação da validação do KYC.

3. **Consulte as rotinas armazenadas**:

   ```http
   GET /api/communications/scheduled
   ```

   Você verá dois registros pendentes (D+15 e D+30) com canal `EMAIL` e regra `CARD_ISSUANCE_REMINDER`.

4. **Simule a emissão do cartão (opcional)** para interromper os lembretes:

   ```http
   PUT /api/customers/12345/card-status
   Content-Type: application/json

   {
     "cardIssued": true
   }
   ```

   Caso o cartão seja emitido antes do D+15 ou D+30, o `ScheduledCommunicationProcessor` marcará
   o lembrete como `SKIPPED`, respeitando a regra de negócio.

5. **Execute o scheduler manualmente** (durante a POC basta aguardar o horário configurado ou
   ajustar a expressão cron em `application.yml`) para que os lembretes sejam avaliados e enviados
   pelo `FakeBrokerService`.

## Exemplo de payload genérico

O endpoint `/api/communications/triggers` continua disponível para cenários personalizados. Exemplo:

```json
{
  "customerId": "67890",
  "subject": "Cartão cancelado",
  "body": "Seu cartão foi cancelado conforme solicitado.",
  "rule": "NONE",
  "sendImmediately": true,
  "channels": ["EMAIL", "SMS"],
  "scheduleDays": [],
  "attributes": {
    "template": "card-cancelled"
  }
}
```

## Simuladores de envio

Dois serviços fictícios representando integrações externas são expostos via logs:

* `FakeBrokerService`: envia e-mail, SMS e WhatsApp.
* `FakePushNotificationService`: envia notificações push.

O scheduler (`ScheduledCommunicationProcessor`) roda diariamente (configurável via `scheduler.communication.cron`)
para avaliar e enviar comunicações pendentes.

> **Nota:** a versão 3.3.4 do Spring Boot foi utilizada por ser a versão estável mais recente disponível nos repositórios públicos
> durante a construção desta POC.
