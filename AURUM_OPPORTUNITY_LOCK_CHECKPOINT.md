# Aurum AI 37 — Opportunity Lock v0.8.2

## Motivo

Teste físico da v0.8.1 no Samsung S25 FE mostrou radar útil e confluências visíveis, porém cadência prática de sinal próxima de zero em sessões longas. O problema foi isolado na política de publicação do Aurum Lock, não no radar.

## Objetivo

Aproveitar oportunidades realmente visíveis sem transformar a Aurum em geradora de sinais frequentes. Não existe quota de sinais por hora. O relógio nunca é critério de entrada.

## Mudanças

- Radar, evidências, pesos e processamento incremental preservados.
- Pré-Lock: Q54; serve apenas como acompanhamento visual.
- Lock base: Q62 + Readiness 58 + 3 famílias independentes + estabilidade + Fast Audit + margem/consistência + baixa contradição.
- Opportunity Lock: permite Lock confirmado abaixo de Q62 apenas quando existem 4 famílias independentes, Q >= 58, Readiness >= 54, estabilidade >= 2 atualizações, margem >= 8, consistência 5/10/30 >= 50, contradição <= 45 e regime estrutural válido.
- Instant Lock continua excepcional: Q76+, qualidade base e regime PEAK.
- TRANSITION, DECAY, SATURATION e falha de captura continuam vetos.
- Raio-X passa a mostrar o principal motivo da trava.
- Correção de safe-area superior para Android 15/16, evitando o título sob a barra de status.

## Filosofia

Mais oportunidades não significa forçar entradas. O v0.8.2 reduz o gargalo artificial do Q80/Readiness82 e exige confluência real, diversidade e persistência.

## Segurança de desenvolvimento

- v0.8.1 permanece preservada.
- v0.8.2 está isolada em `feature/aurum-opportunity-lock-v082`.
- Teste físico continua obrigatório antes de merge final.

## Validação local executada

- `V082_OPPORTUNITY_LOCK_STATIC=PASS`.
- `AURUM_LOCK_CORE=PASS`.
- `AURUM_LOCK_BRIDGE=PASS`.
- `AURUM_LOCK_HOST_PERF=PASS`.
- Regressões v0.7.6/v0.7.7/v0.7.8/v0.7.11/v0.7.12 executadas e aprovadas no host.
- Build Android completo delegado ao GitHub Actions por indisponibilidade de rede no container local.
