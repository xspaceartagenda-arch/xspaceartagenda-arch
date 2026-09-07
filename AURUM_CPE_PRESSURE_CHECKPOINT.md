# Aurum AI 37 — CPE Pressure v0.8.4

## Objetivo
Adicionar a leitura condicional discutida no projeto sem sacrificar a estabilidade da v0.8.3 Capture Integrity.

## Implementado
- Conditional Prediction Engine (CPE) com ranking relativo dos 37 números.
- Leitura hierárquica: setor físico → terminal → números.
- Janelas 5/10/30/60/150/500.
- Transição número→número com shrinkage por amostra.
- Transição terminal→próximo número.
- Pattern Echo em janelas 3/4/5, incluindo exato, terminal, vizinhança e estrutura.
- Aceleração multijanela e pressão de terminal.
- Relações matemáticas somente quando a mesma regra apresenta recorrência histórica mínima; contas retrospectivas arbitrárias são rejeitadas.
- Penalidade para evidência solitária e recompensa limitada para convergência de fontes.
- Ranking Top 5, gap entre líder e vice, qualidade amostral, setor líder/segundo e terminal líder/segundo.
- Novo MAPA DE PRESSÃO DA LEITURA e RAIO-X DO PRÓXIMO GIRO.

## Segurança estatística / de produto
Scores CPE 0–100 são pressão relativa e nunca porcentagem de chance. Em roleta justa, histórico não torna um número obrigatório. A v0.8.4 mantém o CPE em SHADOW: a leitura é calculada e exibida prospectivamente, mas não altera AurumLockPolicy nem publica uma entrada por conta própria. Isso permite comparar o CPE contra resultados futuros antes de promover qualquer peso ao sinal real.

## Preservado
- v0.8.2 Opportunity Lock.
- v0.8.3 Capture Integrity / heartbeat / watchdog / Auto-Resync / catch-up até 12.
- Premium UI, Mini Bubble, assinatura e package.
- CORE estratégico sem alterações.

## Próximo critério de promoção
Registrar CPE prospectivamente e medir Top1, Top3, setor5, cobertura, false confidence, lift e estabilidade. Somente promover ao AURUM LOCK se melhorar fora da amostra e em teste físico/prospectivo.
