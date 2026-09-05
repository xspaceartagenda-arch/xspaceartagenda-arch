# Aurum Lock — checkpoint final v0.8.0

Atualizado em: 2026-09-05 (UTC)

## Estado real

- Branch: `feature/aurum-lock-fast-signal`.
- Base funcional: commit `e0c3fb02f1e2bf100b637a2d636eeeeb03411ea3`.
- Composição: v0.7.11 Performance Incremental + overlay v0.7.12 Pattern Echo.
- Implementação publicada: commit `fb9546aa0693d4b28cfa974227072623f37f2787`.
- Workflow aprovado: GitHub Actions `33962958803`.
- Artefato: `Aurum-AI-37-v0.8.0-Aurum-Lock-Fast-Signal` (ID `9968538195`).
- Pull Request aberto e revisável: `#1`.
- APK: `Aurum-AI-37-v0.8.0-Aurum-Lock-Fast-Signal.apk`.
- Package: `com.xspaceart.aurumai37.integratedlab.debug`.
- Version code: `14`.
- Version name: `0.8.0-aurum-lock-debug`.
- SHA-256 do APK: `10aed066aabb8be08f155f1ada17dd41754e0ad676e5fb9085343ce4f8405a0b`.

## Concluído

- Feature flag `aurum_lock_fast_signal_v1`, ativa por padrão e com fallback legado preservado.
- Índice incremental de 500 giros, janelas 5/10/30 e matriz de transição por delta.
- Vetores normalizados por massa para famílias física, numérica, sequencial e Pattern Echo.
- Evidence Provenance compacto com redução de dependências e Pattern Echo contado uma vez.
- Structural Q separado de Readiness; Q é qualidade estrutural, não probabilidade.
- Regime unidirecional, sem voto próprio nem realimentação sobre Q.
- Data Quality Gate como veto obrigatório.
- Pré-Lock silencioso, Instant Lock, Confirmed Lock e auditoria rápida obrigatória.
- Validade dinâmica máxima de três giros e cancelamento antecipado em perda estrutural.
- Integração real em `ScreenCaptureService`, `AurumLockSession`, `StandaloneActivity`,
  `IntegratedActivity`, bridge DOM/manual e bolha/overlay.
- Histórico, OCR, DOM, WebView, banco, replay, Dual Target, modos existentes,
  Raio-X, Shadow, Missed Opportunity e motor legado preservados.
- Pattern Echo permanece acessível dentro do app e não cria segundo launcher.
- Correção da ordenação decrescente das concentrações do Pattern Echo.

## Testes executados

- Regressão host completa: 21 testes herdados + 3 testes Aurum Lock, todos aprovados.
- Casos Aurum: incremental versus rebuild, limite 500, massa normalizada,
  provenance/anti-duplicação, determinismo, Q, Readiness, Regime, Instant,
  Confirmed, saturation, decay, Data Quality Gate, validade três e bridge real.
- Compilação direta de todas as fontes Java Android contra SDK 35: aprovada.
- Host performance (500 amostras): aprovada abaixo do orçamento de 100 ms;
  os números são somente da JVM host, não do aparelho físico.
- Build Gradle Android limpo: aprovado.
- APK: package, versionamento, único launcher, assinatura, continuidade do signer,
  zipalign e integridade ZIP aprovados.

## Arquivos entregues

- `Aurum-AI-37-v0.8.0-AURUM-LOCK-FAST-SIGNAL-OVERLAY.zip`.
- `.github/workflows/build-aurum-lock-fast-signal.yml`.
- Novas classes em `app/src/main/java/com/xspaceart/radar30/lock/`.
- Integrações em `AppStateStore`, `ScreenCaptureService`, `StandaloneActivity`,
  `IntegratedActivity`, `IntegratedSignalBridge`, manifesto e Gradle.
- Testes `AurumLockCoreTest`, `AurumLockBridgeTest`,
  `AurumLockPerformanceTest` e validador estático.

## Erros corrigidos

- Ordem invertida do ranking agregado do Pattern Echo.
- Segundo ícone launcher do Pattern Echo.
- Capturas rejeitadas agora bloqueiam o Lock em vez de apenas reduzirem score.
- Fluxos OCR, DOM, bootstrap, failover, correção e troca de sessão sincronizam o
  mesmo estado Aurum Lock.
- Sinal novo deixa de herdar validade legada de até cinco giros.

## Pendências reais

- Instalar e validar manualmente o APK no Samsung S25 FE sobre a v0.7.12.
- Medir p50/p95, CPU, memória, temperatura e comportamento OCR no aparelho real.
- Pattern Library persistente e Champion/Challenger permanecem fora desta versão;
  não foram improvisados para não atrasar nem desestabilizar o APK.

`PHYSICAL_S25FE=PENDING`
