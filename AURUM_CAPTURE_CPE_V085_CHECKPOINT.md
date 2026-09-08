# Aurum AI 37 — Capture Continuity + CPE Active v0.8.5

## Objetivo
Corrigir a falha observada em uso real na atualização automática dos resultados e promover a leitura CPE da v0.8.4 de SHADOW para influência ativa, sem permitir que ela domine sozinha o Aurum Lock.

## Captura / OCR
- Heartbeat OCR reduzido para ~1,4 s em ROI estável.
- Parser rejeita relógio/contador com `:` `/` `%` e filtra linhas que não se parecem com a grade.
- `ScanStabilizer` confirma duas leituras quase idênticas, tolerando até duas oscilações antigas nas 12 primeiras células, mas exige o resultado mais recente igual.
- Proteção LIVE: no fluxo normal o synchronizer aceita no máximo **1** giro novo por atualização.
- Catch-up de 2..12 giros só é liberado após falha real/intervalo de captura ou ressincronização explícita.
- Repetição legítima do mesmo número (ex.: 9→9) continua reconhecida.
- Watchdog, Auto-Resync, stale-bubble guard e recuperação da v0.8.3 preservados.

## CPE ativo
- Ranking dos 37, setores, terminais, transições, Pattern Echo, aceleração multijanela e aritmética validada permanecem.
- O vetor CPE entra na fusão final com peso adaptativo limitado (aprox. 0,08–0,22), dependente de qualidade amostral, separação do líder e força do setor.
- CPE **não conta como nova família independente**, evitando inflar artificialmente a confluência.
- Quando o CPE está muito forte e diverge claramente do alvo estrutural, funciona como veto de precisão em vez de forçar outro número.
- Interface passa a mostrar `CPE ATIVO`.
- Scores continuam sendo pressão estrutural relativa, não porcentagem de chance.

## Segurança
A roleta pode ser aleatória; a atualização melhora coerência da captura e a forma como as leituras são combinadas, mas não cria garantia de previsão.

## Validação GitHub Actions
- Workflow: `Build Aurum v0.8.5 Capture + CPE Active`
- Run: `34172968143` — **SUCCESS**
- Commit testado: `d97b9a457bd42cdaa692a96106c35c124db7c55b`
- Artifact ID: `10036336683`
- VersionCode: `19`
- VersionName: `0.8.5-capture-cpe-active-debug`
- APK: `Aurum-AI-37-v0.8.5-Capture-CPE-Active.apk`
- APK SHA-256: `b96d20e06306aff48a0054a3d02e0230582b19bf8ec23cbc05efa44eb9096e5a`
- Artifact ZIP digest: `sha256:269bf9a5650c9d2e7a142cd6be0bbd3e65a73c75c8d15094190e6d3697033d06`

### Gates aprovados
- `OCR_HEARTBEAT_1400=PASS`
- `CLOCK_COUNTER_FILTER=PASS`
- `JITTER_STABILIZER=PASS`
- `SINGLE_SPIN_LIVE_GUARD=PASS`
- `CATCHUP_2_12_RECOVERY=PRESERVED`
- `CPE_MODE=ACTIVE_ASSIST`
- `CPE_WEIGHT_BOUNDED=PASS`
- `CPE_DIVERGENCE_VETO=PASS`
- `FULL_HOST_REGRESSION=PASS`
- `CAPTURE_INTEGRITY=PRESERVED`
- `PREMIUM_UI=PRESERVED`
- `MINI_BUBBLE=PRESERVED`
- `PACKAGE=PASS`
- `SIGNATURE=PASS`
- `UPDATE_COMPATIBILITY=PASS`
- `ALIGNMENT=PASS`

## Próximo passo
Teste físico no Samsung S25 FE continua obrigatório. Validar principalmente uma sequência contínua de 10–20 giros com a bolha flutuante, sem reset/refresh/manual, verificando que cada giro entra uma vez, que strings como `9:31` não criam resultados fantasmas e que o Auto-Resync recupera apenas quando houver um gap real.

`V085_BUILD=PASS`
`PHYSICAL_S25FE=PENDING`
