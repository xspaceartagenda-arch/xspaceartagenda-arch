# Aurum AI 37 — Capture Integrity v0.8.3

Atualizado em: 2026-09-07

## Checkpoint 0 — concluído em código/CI

Base preservada: **v0.8.2 Opportunity Lock** (`feature/aurum-opportunity-lock-v082`).

Branch isolada desta atualização: `feature/aurum-capture-integrity-v083`.

Objetivo: eliminar o caso real em que a Mini Bubble continua flutuando, um novo resultado aparece na mesa, mas o histórico/OCR não atualiza até o operador forçar uma atualização manual.

## Diagnóstico

A v0.8.2 já preservava o pipeline incremental da v0.7.11, incluindo latest-frame, visual-change gate, duas leituras de confirmação, fast resync e catch-up de até 12 resultados. Ainda existiam três caminhos de congelamento operacional:

1. mudança visual pequena podia não atingir o limiar do sampler;
2. uma leitura OCR parcial/falha podia consumir a mudança visual e deixar a ROI parecendo estática nos frames seguintes, sem heartbeat periódico obrigatório;
3. OCR ocupado ou stream de frames parado não tinham watchdog de recuperação próprio.

## Implementado

- OCR safety heartbeat a cada ~2,2 s quando não houve leitura finalizada recentemente;
- sampler visual mais sensível (26x14, delta 20, 2 amostras alteradas);
- watchdog de OCR travado (~4,5 s), invalidando callback antigo e recriando o recognizer;
- watchdog de frames (~4,2 s), recriando ImageReader/VirtualDisplay no mesmo MediaProjection quando necessário;
- auto-resync sem depender da tela principal estar aberta;
- OCR parcial faz pequeno burst de tentativas e depois volta para heartbeat, evitando loop pesado;
- recuperação de resultados perdidos continua usando o ScanSynchronizer, com catch-up de até 12 giros preservado;
- status de captura persistido: LIVE / RESYNC / ERRO / PARADO;
- Mini Bubble deixa de apresentar alvo antigo como confiável durante recuperação: mostra `… / SYNC`; em erro, `! / ERR`; retorna a LIVE após reconciliação;
- tela principal mostra estado de captura, idade do último OCR e quantidade de giros recuperados;
- indicação de giros recuperados permanece visível por alguns segundos;
- brain de sinal v0.8.2, CORE, Aurum Lock, Pattern Echo e keystore preservados por hash no workflow.

## Validação GitHub Actions

Workflow: `Build Aurum v0.8.3 Capture Integrity`

Run: `34169839343` — **SUCCESS**

Commit: `5f1e47f09f7e5326978e13eaf3c70579423c1909`

- `V083_CAPTURE_INTEGRITY_STATIC=PASS`
- `OCR_HEARTBEAT=PASS`
- `OCR_STALL_WATCHDOG=PASS`
- `FRAME_STALL_WATCHDOG=PASS`
- `AUTO_RESYNC=PASS`
- `CATCHUP_UP_TO_12=PRESERVED`
- `STALE_BUBBLE_GUARD=PASS`
- regressão host completa: PASS
- Gradle Android: BUILD SUCCESSFUL
- Premium UI preservada
- Mini Bubble preservada
- package/launcher/signature/zipalign: PASS

## APK

Artifact ID: `10035374893`

Artifact: `Aurum-AI-37-v0.8.3-Capture-Integrity`

APK: `Aurum-AI-37-v0.8.3-Capture-Integrity.apk`

VersionCode: `17`

VersionName: `0.8.3-capture-integrity-debug`

APK SHA-256: `a7a0eb29f583dd8539ab2c0134fd9986c7e5a153f655de5813a9918abcbe2563`

## Próximo teste físico

1. Instalar por cima da v0.8.2, sem desinstalar/limpar dados.
2. Iniciar a captura e deixar somente a Mini Bubble flutuando durante sessão real.
3. Confirmar que cada giro aparece automaticamente sem abrir a Aurum para atualizar.
4. Simular retorno da mesa/aba e observar `SYNC` -> `LIVE`.
5. Se houver giro perdido durante uma falha transitória, confirmar que a tela informa `+N recuperados` e que a sequência é reconciliada na ordem correta.
6. Só depois do teste físico seguir para o Checkpoint 1: CPE Shadow / ranking dos 37 + novo Raio-X da leitura.

`CHECKPOINT_0_CAPTURE_INTEGRITY=PASS_CI`

`PHYSICAL_S25FE=PENDING`
