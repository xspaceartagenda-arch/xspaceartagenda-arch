# Aurum AI 37 — Opportunity Lock v0.8.2

Atualizado em: 2026-09-06

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

## Validação final

GitHub Actions workflow: `Build Aurum v0.8.2 Opportunity Lock`

Run: `34023021531` — SUCCESS
Commit testado: `bbf94a3de0835081d8b18ee56cb8543103aba012`

- `V082_OPPORTUNITY_LOCK_STATIC=PASS`
- `RADAR_ENGINE=PRESERVED`
- `MULTI_CONFLUENCE_GATE=PASS`
- `NO_TIME_BASED_SIGNAL_QUOTA=PASS`
- Regressão host completa: 24/24 PASS
- `AURUM_LOCK_CORE=PASS`
- `AURUM_LOCK_BRIDGE=PASS`
- `AURUM_LOCK_HOST_PERF=PASS`
- Gradle Android: `BUILD SUCCESSFUL`
- Premium UI preservada
- Mini Bubble preservada
- Safe-area superior: PASS
- Launcher único: PASS
- Package auditado: PASS
- Assinatura: PASS
- Compatibilidade de atualização: PASS
- Zipalign: PASS

## APK

Artifact ID: `9986160416`
Artifact: `Aurum-AI-37-v0.8.2-Opportunity-Lock`
APK: `Aurum-AI-37-v0.8.2-Opportunity-Lock.apk`
VersionCode: `16`
VersionName: `0.8.2-opportunity-lock-debug`
APK SHA-256: `4e965d76338cfc33475301cf3fbded272132e7ccc228766fbbb0b6f4c9056e2e`
Artifact ZIP SHA-256: `06ccd7551055afab1eff89c8c8728089dfc08cf70716a309b9e6e167642737c3`

## Próximo passo exato

1. Instalar a v0.8.2 no Samsung S25 FE sobre a versão atual.
2. Confirmar abertura, OCR, atualização do radar, modo flutuante e Mini Bubble.
3. Fazer sessão real e observar principalmente a cadência dos sinais, motivos de bloqueio e qualidade das oportunidades liberadas.
4. Não fazer merge final antes do teste físico.

`V082_BUILD=PASS`

`APK_V082=PASS`

`PHYSICAL_S25FE=PENDING`
