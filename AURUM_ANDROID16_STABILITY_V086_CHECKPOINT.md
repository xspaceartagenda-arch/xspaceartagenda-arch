# Aurum AI 37 — Android 16 Capture Stability v0.8.6

Atualizado em: 2026-09-09

## Estado do checkpoint

- Branch: `feature/aurum-android16-stability-v086`
- Base preservada: v0.8.5 Capture + CPE Active
- Nova versão: `versionCode 20`, `versionName 0.8.6-android16-capture-stability`
- Algoritmos Aurum Lock, CPE, Radar, OCR e regras de sinal: **inalterados**
- Validação estática específica: **PASS**
- Build Android/GitHub Actions: **SUCCESS**
- Teste físico no Samsung/Android 16: **PENDENTE**

## Diagnóstico confirmado no bugreport

Versão afetada: v0.8.5 (`versionCode 19`, target SDK 35), Android 16.

Falha principal, repetida em vários horários:

```text
FATAL EXCEPTION: main
Unable to start service com.xspaceart.radar30.ScreenCaptureService
Intent action=com.xspaceart.radar30.RESET (também ocorreu com STOP)
Caused by: java.lang.SecurityException
Starting FGS with type mediaProjection requires android:project_media
at ScreenCaptureService.ensureForeground(ScreenCaptureService.java:1137)
at ScreenCaptureService.onStartCommand(ScreenCaptureService.java:170)
```

Causa: `onStartCommand()` chamava `ensureForeground()` para **todo** comando. Ao
usar LIMPAR/PARAR sem uma autorização MediaProjection vigente, o Android 16
bloqueava a promoção do serviço e encerrava o app. A permissão do Manifest já
existia; adicionar outra permissão não resolveria, porque `project_media` é a
autorização temporária entregue após o consentimento de captura.

O relatório também preservou duas falhas de estabilidade relacionadas:

1. Tombstones antigos com `SIGSEGV` em `Bitmap.copyPixelsFromBuffer()` na thread
   `radar30-capture`, causados pela cópia direta de um plano RGBA cujo padding da
   última linha pode não existir no buffer Samsung.
2. ANR antigo em `StandaloneActivity.refreshState()` ao fazer parse síncrono do
   JSON de `SessionLedger.sessionSummary()` na thread principal durante uma fila
   de broadcasts de estado.

## Correções implementadas

- `startForeground(mediaProjection)` ocorre somente no `ACTION_START`, depois de
  validar um resultado de consentimento novo e antes de `getMediaProjection()`.
- `RESET`, `STOP`, `MANUAL`, correções, toggle da bolha e fast-resync usam
  `startService()` e nunca tentam criar um MediaProjection FGS sem autorização.
- Autorização ausente/expirada encerra o serviço com estado recuperável, sem crash.
- Notificação de captura não é recriada quando não existe projeção ativa.
- Frames RGBA são compactados linha a linha em buffer próprio antes da cópia para
  Bitmap; a leitura nunca ultrapassa o limite real do `Image.Plane`.
- Resumo de sessão passa a ser processado em executor dedicado, fora da UI.
- Broadcasts consecutivos da tela Standalone são agrupados com debounce de 150 ms.
- `versionCode` incrementado para permitir atualização sobre a v0.8.5.

## Arquivos da atualização

- `v086-src/patch/patch_00.b64`
- `v086-src/tools/apply_v086_project.py`
- `v086-src/tools/v086_android16_stability_validation.py`
- `.github/workflows/build-aurum-android16-stability-v086.yml`

## Gates já aprovados

- `V086_VERSION=PASS`
- `V086_FOREGROUND_ORDER=PASS`
- `V086_EXPIRED_GRANT_FALLBACK=PASS`
- `V086_IDLE_NOTIFICATION_GUARD=PASS`
- `V086_SAFE_FRAME_COPY=PASS`
- `V086_CONSENT_STARTS_ONLY=PASS`
- `V086_CONTROL_COMMANDS=PASS`
- `V086_UI_THREAD_LEDGER=PASS`
- `V086_ANDROID16_STABILITY=PASS`
- Aplicação limpa do patch sobre uma composição v0.8.5: **PASS**

## Build validado

- Workflow: `Build Aurum v0.8.6 Android 16 Capture Stability`
- Run: `34318826093` — **SUCCESS**
- Commit testado: `d06ea455efebbda55a89eed0c199a829da42198a`
- Artifact ID: `10091123369`
- Artifact: `Aurum-AI-37-v0.8.6-Android16-Stability`
- Artifact ZIP SHA-256: `345b8b5df3698e311c01dde437d556b45c6ae19193038861d06c6d9508d16b73`
- APK: `Aurum-AI-37-v0.8.6-Android16-Stability.apk`
- APK SHA-256: `4755752f906aded8b2556489af00e75527554a15e788590f9f5043ff3dbd9d23`
- Package: `com.xspaceart.aurumai37.integratedlab.debug`
- Assinatura de atualização preservada: **PASS**
- Full host regression: **PASS**
- Build Android, zipalign e auditoria do APK: **PASS**
- Artifact disponível até: `2026-10-09`

## Próximo passo exato

1. Instalar por cima da v0.8.5 e testar: LIMPAR com OCR parado, PARAR, nova
   autorização, 10–20 giros com bolha e bloqueio/desbloqueio da tela.

`V086_CHECKPOINT=CI_PASS_PHYSICAL_PENDING`
