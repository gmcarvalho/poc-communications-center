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

### Exemplo de payload de evento

```json
{
  "customerId": "12345",
  "subject": "Validação KYC concluída",
  "body": "Seu cadastro foi aprovado, finalize a emissão do cartão.",
  "rule": "CARD_ISSUANCE_REMINDER",
  "sendImmediately": true,
  "channels": ["PUSH", "EMAIL"],
  "scheduleDays": [15, 30],
  "attributes": {
    "template": "kyc-success"
  }
}
```

O exemplo acima dispara push e e-mail imediatamente. Além disso, agenda novos disparos para D+15 e D+30
evaluando a regra `CARD_ISSUANCE_REMINDER` (envia somente se o cartão ainda não tiver sido emitido).

## Simuladores de envio

Dois serviços fictícios representando integrações externas são expostos via logs:

* `FakeBrokerService`: envia e-mail, SMS e WhatsApp.
* `FakePushNotificationService`: envia notificações push.

O scheduler (`ScheduledCommunicationProcessor`) roda diariamente (configurável via `scheduler.communication.cron`)
para avaliar e enviar comunicações pendentes.

> **Nota:** a versão 3.3.4 do Spring Boot foi utilizada por ser a versão estável mais recente disponível nos repositórios públicos
> durante a construção desta POC.
