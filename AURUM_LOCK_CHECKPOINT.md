# Aurum Lock — checkpoint de continuidade

Atualizado em: 2026-09-05 (UTC)

## Como continuar em outro chat

Peça para continuar o trabalho a partir deste arquivo na branch
`feature/aurum-lock-fast-signal` do repositório
`xspaceartagenda-arch/xspaceartagenda-arch`.

Antes de editar, confirme o HEAD da branch, leia este checkpoint e preserve a
`main` sem alterações.

## Estado confirmado

- Branch de trabalho: `feature/aurum-lock-fast-signal`.
- Commit-base funcional: `e0c3fb02f1e2bf100b637a2d636eeeeb03411ea3`.
- Base composta e validada: v0.7.11 Performance Incremental + overlay v0.7.12
  Pattern Echo.
- Pacotes-base:
  - `Aurum-AI-37-v0.7.11-PERFORMANCE-INCREMENTAL-GITHUB-BUILD.zip`
  - `Aurum-AI-37-v0.7.12-PATTERN-ECHO-OVERLAY.zip`
- A composição reproduz o código-fonte do build oficial aprovado da v0.7.12.
- Baseline: 21 testes Java host aprovados e APK oficial v0.7.12 aprovado.
- Benchmark físico no Samsung S25 FE: `PENDING`.

## Decisões obrigatórias

- Feature flag: `aurum_lock_fast_signal_v1`.
- Fluxo determinístico e unidirecional:
  `Family Scores -> Structural Q -> Regime/Readiness -> Lock`.
- Regime e dimensão temporal não contam como famílias independentes.
- Pattern Echo pode apoiar a decisão, mas conta no máximo uma vez.
- Q mede qualidade estrutural, nunca probabilidade de vitória.
- Data Quality Gate é veto obrigatório, não desconto de Q.
- Validade dinâmica máxima: três giros; cancelar em transição, decadência,
  queda de Q/Readiness, troca do rival ou captura inválida.
- Instant Lock não deve aguardar giro extra quando o estado já estiver maduro.
- Mesmo histórico + mesma configuração + mesma versão = mesmo resultado.
- Preservar captura DOM, OCR, manual, histórico, replay, banco, UI e motor atual.

## Prioridade de entrega

1. Núcleo incremental e provenance compacto.
2. Structural Q, Readiness, Regime e Fast Audit obrigatório.
3. Pré-Lock discreto, Instant/Confirmed Lock e validade dinâmica.
4. Integração na UI/bolha e APK instalável.
5. Pattern Library e Champion/Challenger somente se não atrasarem o app.

## Inconsistências conhecidas da baseline

- Pattern Echo ainda não participa do sinal vivo.
- Ordenação do Pattern Echo pode colocar menor suporte primeiro.
- Pattern Echo está registrado como segundo launcher.
- Terminal atual pode permanecer por cinco giros, acima do novo máximo de três.
- Testes vivem no workflow como Java host, não em `app/src/test`.
- Material de assinatura existente deve ser preservado sem reproduzir segredos.

## Estado da implementação

- Auditoria e composição da baseline: `CONCLUÍDO`.
- Branch remota de feature: `CRIADA`.
- Mapeamento dos pontos de integração: `CONCLUÍDO`.
- Testes de regressão do Aurum Lock: `EM IMPLEMENTAÇÃO`.
- Núcleo do Aurum Lock: `PENDING`.
- Integração Android/UI: `PENDING`.
- Novo build/APK: `PENDING`.
- Pull Request: `PENDING`.

## Próximo passo exato

Adicionar testes Java host para atualização incremental, normalização de massa,
provenance/anti-duplicação, determinismo, Q/Readiness/Regime, Instant Lock,
Confirmed Lock, saturation/decay, veto de captura e validade máxima de três;
em seguida implementar as classes mínimas para fazê-los passar.

Não declarar código, build, APK, benchmark ou PR como concluído sem evidência.
