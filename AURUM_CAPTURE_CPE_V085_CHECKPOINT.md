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

## Validação
Pendente GitHub Actions e teste físico no Samsung S25 FE.
